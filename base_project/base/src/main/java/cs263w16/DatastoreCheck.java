package cs263w16;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class DatastoreCheck {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();  
	
	public DatastoreCheck(){
		syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	}
	
	public boolean check(String kind, Key entityKey) {
		// TODO Auto-generated method stub
		
		Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, entityKey);
        Query keyBasedQuery = new Query(kind).setFilter(keyFilter);
        List<Entity> results = datastore.prepare(keyBasedQuery).asList(FetchOptions.Builder.withDefaults());
		
		if(results.isEmpty()) {
			
			Entity ent=new Entity(kind,entityKey);
			
			datastore.put(ent);
			if(syncCache.get(entityKey.getName())==null) 
			{
				syncCache.put(entityKey.getName(),ent);
			}
			
			return false;
		}
		else 
		{
			if(syncCache.get(entityKey.getName())==null)
			{
				syncCache.put(results.get(0),entityKey.getName());
			}
			
			return true;
		}
	}
}
