package meli.simian.domain.service.impl;

import meli.simian.domain.exception.InternalArchitectureException;
import meli.simian.domain.service.DnaHasherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class DnaHasherServiceImpl implements DnaHasherService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public String hash(String[] dna) {
        // ensures {A, A, A, A} will have a different hash from {AA, AA}
        String joined = String.join(",", dna);

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHsA-1");

            digest.reset();
            digest.update(joined.getBytes("utf8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error(String.format("Error while hashing the DNA sequence, raising InternalArchitectureException. dna=%s", Arrays.toString(dna)));
            throw new InternalArchitectureException("Error while hashing DNA sequence. Please contact the developer(s)");
        }
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }
}
