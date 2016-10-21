package net.aqualogybs.crud;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.IOException;
import java.util.List;

//import static io.vertx.core.Vertx.vertx;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());


        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");

        router.get("/crud/:entity").handler(routingContext -> {
            String entityClass = routingContext.request().getParam("entity");
            System.out.println(entityClass);
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");

            EntityManager em = emf.createEntityManager();
            Query q = em.createQuery("from " + entityClass + " c");
            List l = q.getResultList();
            String jsonInString = null;
            try {
                jsonInString = mapper.writeValueAsString(l);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            response.end(jsonInString);
        });

        router.get("/crud/:entity/:id").handler(ctx ->{
            String entityClass = ctx.request().getParam("entity");
            Integer id = Integer.parseInt(ctx.request().getParam("id"));
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "application/json");
            EntityManager em = emf.createEntityManager();
            try {
                Class cls = Class.forName("net.aqualogybs.crud.entities."+entityClass);
                Object entity = em.find(cls,id);
                String jsonInString = mapper.writeValueAsString(entity);
                response.end(jsonInString);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


        });

        router.delete("/crud/:entity/:id").handler(ctx ->{
            String entityClass = ctx.request().getParam("entity");
            Integer id = Integer.parseInt(ctx.request().getParam("id"));
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "application/json");
            EntityManager em = emf.createEntityManager();
            try {
                Class cls = Class.forName("net.aqualogybs.crud.entities."+entityClass);
                Object entity = em.find(cls,id);
                em.getTransaction().begin();
                em.remove(entity);
                em.getTransaction().commit();
                response.end( "{\"id\":" + id + ",\"action\":\"delete\"}");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        });

        router.put("/crud/:entity/:id").handler(ctx ->{
            String entityClass = ctx.request().getParam("entity");
            Integer id = Integer.parseInt(ctx.request().getParam("id"));
            //String body = ctx.getBodyAsString();
            JsonObject obj = ctx.getBodyAsJson();
            obj.put("id",id);
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "application/json");
            EntityManager em = emf.createEntityManager();
            try {
                Class cls = Class.forName("net.aqualogybs.crud.entities."+entityClass);
                Object entity2 = mapper.readValue(obj.toString(), cls);
                //Object entity = em.find(cls,id);
                em.getTransaction().begin();
                em.merge(entity2);
                em.getTransaction().commit();
                response.end( "{\"id\":" + id + ",\"action\":\"update\"}");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        router.post("/crud/:entity").handler(routingContext -> {
            String entityClass = routingContext.request().getParam("entity");
            String body = routingContext.getBodyAsString();
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");

            try {
                Class cls = Class.forName("net.aqualogybs.crud.entities."+entityClass);
                Object entity = mapper.readValue(body, cls);
                System.out.println(entity);

                EntityManager em = emf.createEntityManager();
                em.getTransaction().begin();
                em.persist(entity);
                em.getTransaction().commit();


                em.close();

                response.end("Hello Post " + entityClass);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();

                response.end("Hello Post " + e.getMessage());
            } catch (JsonParseException e) {
                e.printStackTrace();

                response.end("Hello Post " + e.getMessage());
            } catch (JsonMappingException e) {
                e.printStackTrace();

                response.end("Hello Post " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();

                response.end("Hello Post " + e.getMessage());
            }

        });

        server.requestHandler(router::accept).listen(8080);
        System.out.println( "Start Server!" );
    }
}
