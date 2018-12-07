package org.superbiz.moviefun.albums;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {

        Blob blob = new Blob(""+albumId, uploadedFile.getInputStream(), uploadedFile.getContentType());
        blobStore.put(blob);

        return format("redirect:/albums/%d", albumId);
    }

    private HttpEntity<byte[]> constructHttpResponse(InputStream stream, String contentType) throws IOException {
        byte[] bytes = StreamUtils.copyToByteArray(stream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(bytes.length);

        return new HttpEntity<>(bytes, headers);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException {

        Optional<Blob> maybeBlob = blobStore.get("" + albumId);
        if (maybeBlob.isPresent()) {
            Blob blob = maybeBlob.get();
            return constructHttpResponse(blob.inputStream, blob.contentType);
        } else {
            ClassLoader loader = getClass().getClassLoader();
            InputStream stream = loader.getResourceAsStream("default-cover.jpg");
            return constructHttpResponse(stream, MediaType.IMAGE_JPEG_VALUE);
        }
    }

}
