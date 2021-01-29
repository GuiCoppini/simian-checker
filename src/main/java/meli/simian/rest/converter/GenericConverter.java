package meli.simian.rest.converter;

public interface GenericConverter<R, S> {
    S convert(R domain);
}
