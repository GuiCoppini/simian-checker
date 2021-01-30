# Simian-Checker
Simian-Checker é uma aplicação escrita em Java + SpringBoot e é capaz de verificar se uma determinada sequência de DNA pertence a um ser Humano ou Mutante.  
Resumidamente, a aplicação utiliza o MongoDB para armazenar os DNAs já computados e evitar que sejam computados novamente. A app também possui testes unitários e de integração, contando com mais de 90% de code coverage nos testes.

### Índice
* [Banco](#banco)
* [Frameworks e dependências](#frameworks-e-dependências)
* [Java 11?](#por-que-Java-11?)
* [Como rodar a aplicação](#como-rodar-a-aplicação)
* [Estrutura da Aplicação](#estrutura-da-aplicação)
* [Como a aplicação funciona?](#como-a-aplicação-funciona?)
* [Testes](#testes)
* [Cache](#cache)
* [Hospedagem](#hospedagem)
* [Documentação/Swagger](#documentação-da-API)
* [Próximos passos](#Próximos-passos-do-projeto)

## Banco
Para persistir informações de DNAs já computados, o Simian-Checker utiliza um MongoDB; banco não-relacional escolhido devido à sua rápida execução de SELECTs e INSERTs. Isso é importante pois a aplicação não conta com DELETEs nem UPDATEs, uma vez que os DNAs são computados e gravados no banco ou apenas buscados do banco caso já existam.

## Frameworks e dependências
A aplicação, como dito anteriormente foi escrita em Java 11 + SpringBoot, a ferramenta de build utilizada foi o Gradle.

A aplicação também utiliza:  
* JUnit - testes unitários
* Mockito - mockar componentes para testes
* Swagger - documentação (será abordado mais à frente)
* Lombok - ajuda a evitar código boilerplate como construtores, getters e setters etc
* EmbeddedMongo - MongoDB embedded utilizado nos testes integrados

### Por que Java 11?
No início, a aplicação estava sendo escrita em Java 8 (por costume meu), porém, o deploy no GCP é muito mais fluido de
fazer quando estamos utilizando Java 11 (Exemplo: com Java 11 é necessário montar um XML com as configurações e o GCP
ignorava tudo que eu tentava colocar). Assim, como não gerou impacto nos fluxos, o Java para o qual o Gradle aponta é o 11
e, para fazer o gradle wrapper funcionar, é necessário settar o path Java 11 na JAVA_HOME, pois o wrapper utiliza o JAVA_HOME. 

## Como rodar a aplicação
Para rodar a aplicação, há, a grosso modo, 2 maneiras:
1) Aplicação em Docker apontando para o banco "produtivo" (será explicado posteriormente);
2) Aplicação e banco ambos Dockerizados.

OBS.: Há 3 profiles (SPRING_PROFILES_ACTIVE) na aplicação:
* default - utilizado para rodar a aplicação local (sem docker) apontando para o bando dockerizado;
* docker - utilizado quando tanto a app quanto o banco estão em docker;
* prod - utilizado para utilizar o banco hospedado no Atlas.

### Aplicação dockerizada apontando para o banco remoto
Para executar a aplicação em docker (expondo a porta 80) e fazê-la apontar para o banco remoto, basta executar o seguinte comando:
```bash
docker run -p 80:8080 -e "SPRING_PROFILES_ACTIVE=prod" simian-checker:latest 
``` 

### Aplicação e banco dockerizados
Para executar tudo em docker, você pode utilizar o docker-compose com os seguintes comandos:
```bash
./gradlew clean build                  # gera o entregável da aplicação, incluindo testes
docker build -t simian-checker .       # monta a imagem Docker que será utilizada pelo docker-compose
docker-compose up                      # sobe o banco e a aplicação, ambos dockerizados
```


## Estrutura da aplicação
A aplicação foi divida principalmente em dois pacotes, o `rest` e o `domain` (também tem o common, mas não é tão "principal" assim).
* rest - contém tudo que diz respeito à camada REST, como anotações de validação, classes de payload, controllers, converters, filters etc;
* domain - contém o `core` da aplicação, como entidades de banco, services, repositórios, exceptions etc;
* common - contém tudo que é utilizado em ambos os pacotes, nesse caso, só contém a lógica de validação de DNA (se a matriz é quadrada, caracteres etc), pois não é uma lógica única de uma camada ou de outra.  
  
A ideia de separar as camadas de tal maneira é para que a camada de aplicação seja independente da camada de interface. Com isso podemos utilizar a lógica da aplicação e mudar o ponto de entrada, como deixar de usar REST e passar a utilizar algum CLI, ou até mesmo outra tecnologia como gRPC, pois só deveria ser reimplementada a lógica da interface.  
Por outro lado, a camada REST conhece alguns objetos da camada de service, pois a camada da interface é quem faz a comunicação do mundo de fora (cliente) com o mundo de dentro (aplicação), mas a aplicação não deve conhecer entidades do mundo "de fora".

![Diagrama](images/diagram.png)

### Como a aplicação funciona?
#### Endpoint `/simian`
O ponto de entrada da aplicação para determinar se um DNA é simio ou não é na classe `SimianCheckerController`; antes que o objeto de request entre na lógica da aplicação, há uma anotação `@ValidDNAMatrix` que validará se o DNA enviado é válido (matrix quadrada, caracteres permitidos etc); caso não seja, a aplicação retornará status 400 e um payload indicando os erros do Request.  
Após isso, o DNA é então enviado ao `OrchestratorService`, service responsável por verificar se o DNA já foi computado ou se deve passar pelo algoritmo de identificação (OBS.: Esse service é cacheado por 15 minutos para evitar hits desnecessários no banco).

O algoritmo `isSimian(String[] dna)` está no service `SimianCheckerService` que é responsável unicamente por identificar se um DNA é simio ou não. Mas antes de iniciar o algoritmo, ele faz uma chamada a um service que faz a validação do DNA (mesmo já tendo sido validado na camada REST), pois como o Service é agnóstico do que a camada de interface faz ou deixa de fazer, ele deve validar o que entra para evitar um erro 500. Então finalmente, ele inicia o algoritmo real.

Sua lógica consiste em quebrar o problema em 3 etapas:
* Verificar se há algum padrão horizontal;
  * Para cada linha da matriz, ele busca iterativamente uma repetição de 4 caracteres.
* Verificar se há algum padrão vertical;
  * Para cada coluna da matriz, ele busca iterativamente uma repetição de 4 caracteres.
* Verificar se há algum padrão diagonal;
  * Para cada caractere da matriz, ele chama 2 métodos recursivos, um para a esquerda e um para a direita buscando algum match de caractere igual e incrementando um contador passado como parâmetro da chamada.

Apesar da recursão ser o pesadelo de muita gente, como o máximo de chamadas empilhadas é 2*4 (esquerda e direita * 4 caracteres no máximo), não gerará problemas como StackOverflow ou afins.  
A complexidade deste algoritmo, por percorrer a matriz praticamente 4 vezes (transformar em uma matriz de char, verificações vertical, horizontal e diagonal percorrendo cada char), é cerca de ```O(4n² + m)``` utilizando n=ordem da matriz e m=caracteres a mais percorridos na verificação diagonal, como m está limitado à ordem da matriz (m é da mesma ordem que n, ou seja, é impossível que ```m=n²```), podemos desprezá-lo e, simplificando a demonstração final, temos ```O(n²)```.

Por fim, após o algoritmo verificar se o DNA é simio ou não, o DNA é digerido em um SHA-1 para que seja salvo no banco sem que ocupe um espaço fortemente variável e que facilite a comparação; e então o Controller faz a verificação se deve retornar 200 ou 403, afinal, a camada REST e ela apenas, tem a responsabilidade sobre como a resposta será montada.

#### Endpoint `/stats`
O endpoint de statistics é bem simples, o StatsController faz a requisição para o StatsService que, por sua vez, utiliza o DnaRepository para que busque no banco a quantidade de DNAs salvos com cada tipo (humano ou mutante), retorna um objeto para o Controller e o Controller , por ser da camada de interface REST, faz a tratativa no objeto e o converte para o objeto de response, podendo omitir campos se precisar.

#### Endpoint `/mgmt/clear`
Este endpoint de Management só foi criado para facilitar na hora da execução dos testes.
Assim, o usuário que está testando a aplicação pode limpar o banco e o cache para testar algumas funcionalidades sem precisar 
aguardar o tempo do reset do Cache e sem precisar logar no Atlas para limpar o banco.

## Testes
No pacote de teste, é possível encontrar testes dos services da aplicação e também do controller da aplicação.

### Unitários
Os testes de services e components em geral são testes unitários, fazendo uso do Mockito para simular chamadas a outras 
classes para que a lógica de outras classes não interfira na classe que está sendo testada.  

### Integração
Os testes de integração estão baseados nos nomes dos Controllers. Vale lembrar que não há nenhum
mock nos testes integrados.  
Para testar o fluxo ponta-a-ponta, eu utilizei o TestRestTemplate para fazer as requisições à API e uma dependência 
que configura um MongoDB embedded para que não seja necessário mockar o repositório.

### Coverage de testes
Para fazer a verificação da cobertura de testes, eu utilizei o plugin do JaCoCo, para gerar um report sobre a cobertura,
basta rodar o seguinte comando
```
./gradlew clean build jacocoTestReport
```
E ver o HTML com os resultados dos testes em build/reports/jacoco/test/html/index.html

OBS.: Para que o JaCoCo fizesse a análise correta da cobertura, foi necessário adicionar um arquivo de configuração do Lombok 
`lombok.config` para que o JaCoCo ignorasse código gerado pelo Lombok, como equals() e hashCode(). Essa anotação faz com 
que o Lombok adicione @Generated ao código gerado e, consequentemente, o JaCoCo os ignora.

Por fim, a cobertura de testes ficou por volta dos 95% segundo o JaCoCo (e segundo o IntelliJ também).

## Cache
Para o cache, eu acabei utilizando o Cache padrão do Spring. As configurações para habilitar um redis estão comentados
na aplicação, porém não as deixei lá pois só funcionaria no ambiente 100% dockerizado, ou seja, não faz sentido dado que 
a aplicação que está hospedada remotamente não possuiria Redis.  
Conclusão: caso seja necessário o Redis para esse projeto, a POC foi feita e basta descomentar as configurações do Redis 
do docker-compose, build.gradle e application*-properties.

## Hospedagem
A hospedagem da aplicação e do banco foram feitas nos seguintes locais:
* Heroku - Muito fácil de subir, fácil de passar variáveis de ambiente e fácil de configurar (e grátis).
* GCP - Subir um Java 8 e um Java 11 são configurações diferentes, Java 8 deu muita dor de cabeça e acabou indo como Java 11.
* Atlas - O MongoDB está hospedado no Atlas. Para consultá-lo, pode usar a String de conexão listada abaixo
no Compass ("Workbench" que utilizei para administrar o MongoDB).
  * O nome do db é `simian_checker_db` e a collection é `dna`

MongoDB - 
```
mongodb+srv://meli_user:meli_passwd@simian-checker-cluster.auxqr.mongodb.net/simian_checker_db
```

Heroku
```
https://simian-checker-app.herokuapp.com/
```

GCP
```
https://simian-checker.ue.r.appspot.com/
```

## Curls essenciais
Aqui vão alguns curls para facilitar na hora de consumir a API:

- Curl para enviar DNA não simio (Humano)
```bash
curl --location --request POST 'https://simian-checker.ue.r.appspot.com/simian' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dna":  ["ACTG", "GACT", "AGGG", "GACT"]
}'
```

- Curl para enviar DNA simio (Mutante)
```bash
curl --location --request POST 'https://simian-checker.ue.r.appspot.com/simian' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dna":  ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
}'
```

- Curl para o endpoint de estatísticas
```
curl --location --request GET 'https://simian-checker.ue.r.appspot.com/stats'
```

- Curl para limpar o banco e/ou o cache
```
curl --location --request PUT 'localhost:8080/mgmt/clear' \
--header 'Content-Type: application/json' \
--data-raw '{
    "clear_cache": true,
    "clear_database": true
}'
```

## Documentação da API
A documentação, como indicado no início, está disponível no Swagger da aplicação:
```
https://simian-checker-app.herokuapp.com/swagger-ui.html
```

## Próximos passos do projeto
Aqui vão algumas ideias que me soaram interessantes porém não fizeram parte do MVP do projeto:
* Task do gradle separada para rodar os testes integrados separadamente
* Trocar o MongoDB embedded pelo TestContainers
* Descrever os testes com @DisplayName
* Colocar autenticação por header no endpoint de /mgmt/
* Elaborar testes sobre cache no teste integrado do /mgmt/clear