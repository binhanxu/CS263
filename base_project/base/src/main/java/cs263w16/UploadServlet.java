package cs263w16;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class UploadServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

    	BlobKey blobKey=new BlobKey(req.getParameter("blob-key"));
    	
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        List<BlobKey> blobKeys = blobs.get("myFile");
        if (blobKeys == null || blobKeys.isEmpty()) {
            res.sendRedirect("/");
        } else {
        	res.getWriter().println(blobKey.getKeyString());
            //res.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
        }
    }
       
}
