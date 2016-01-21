package cs263w16;
import java.util.Date;
import java.util.List;
import javax.management.RuntimeErrorException;
import javax.ws.rs.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class TaskDataResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    String keyname;

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();


    public TaskDataResource(UriInfo uriInfo, Request request, String kname) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.keyname = kname;
    }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public TaskData getTaskDataHTML() {
        // add your code here (get Entity from datastore using this.keyname)
        // throw new RuntimeException("Get: TaskData with " + keyname + " not found");
        // if not found
        try {
			Entity ent;
			if(syncCache.get(keyname)!=null) {
				
				ent=(Entity)syncCache.get(keyname);
				return new TaskData(keyname, 
				(String)ent.getProperty("value"), 
				(Date)ent.getProperty("date"));
        
			}
            ent = datastore.get(KeyFactory.createKey("TaskData", keyname));
            return new TaskData(keyname, 
			(String)ent.getProperty("value"), 
			(Date)ent.getProperty("date"));
			
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Get Taskdata with: " + 
			this.keyname + 
			" not found");
        }

    }

    // for the application
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public TaskData getTaskData() {
        // same code as above method
        return getTaskDataHTML();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putTaskData(String val) {
      
		Key entKey = KeyFactory.createKey("TaskData", keyname);
        Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, entKey);
        Query query = new Query().setFilter(keyFilter);
        List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        
		Response res = null;
        
		if(results.isEmpty()){
            Entity ent = new Entity(KeyFactory.createKey("TaskData", keyname));
            ent.setProperty("value", val);
            ent.setProperty("date", new Date());
            datastore.put(ent);
            
			res = Response.created(uriInfo.getAbsolutePath()).build();
        }
        else
		{
            Entity ent = results.get(0);
            ent.setProperty("value", val);
            ent.setProperty("date", new Date());
            datastore.put(ent);
            
			res = Response.noContent().build();
        }
       
        return res;
    }

    @DELETE
    public void deleteIt() {
        try{
			if(syncCache.get(keyname)!=null) {
				synCache.delete(keyname);				
			}
        datastore.delete(KeyFactory.createKey("TaskData", keyname));
        }catch(IllegalArgumentException e){
           throw new RuntimeException("The key not exists!");
        }
        // delete an entity from the datastore
        // just print a message upon exception (don't throw)
    }
}