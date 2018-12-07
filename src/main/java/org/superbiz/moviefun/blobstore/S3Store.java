package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client s3Client;
    private final String bucketName;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {

        this.s3Client = s3Client;
        this.bucketName = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.contentType);
        s3Client.putObject(bucketName, blob.name, blob.inputStream, metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        boolean objectExists = s3Client.doesObjectExist(bucketName, name);

        if (objectExists) {
            S3Object object = s3Client.getObject(bucketName, name);
            Blob blob = new Blob(name, object.getObjectContent(), object.getObjectMetadata().getContentType());
            return Optional.of(blob);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {

    }
}
