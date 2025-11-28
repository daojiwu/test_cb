package com.example.test_cb.utils;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * 独立运行的MongoDB工具类，无需启动Spring Boot项目即可使用
 * 可以直接通过main方法测试MongoDB功能
 */
public class StandaloneMongoUtil {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 27017;
    private static final String DEFAULT_DATABASE = "test";
    
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    /**
     * 默认构造函数，使用默认配置连接到MongoDB
     */
    public StandaloneMongoUtil() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DATABASE);
    }
    
    /**
     * 构造函数，指定主机、端口和数据库名
     * 
     * @param host MongoDB主机地址
     * @param port MongoDB端口
     * @param databaseName 数据库名
     */
    public StandaloneMongoUtil(String host, int port, String databaseName) {
        try {
            mongoClient = MongoClients.create("mongodb://" + host + ":" + port);
            database = mongoClient.getDatabase(databaseName);
            System.out.println("MongoDB连接成功: " + host + ":" + port + "/" + databaseName);
        } catch (Exception e) {
            System.err.println("MongoDB连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 构造函数，使用完整的连接字符串
     * 
     * @param connectionString MongoDB连接字符串
     * @param databaseName 数据库名
     */
    public StandaloneMongoUtil(String connectionString, String databaseName) {
        try {
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
            System.out.println("MongoDB连接成功: " + connectionString + "/" + databaseName);
        } catch (Exception e) {
            System.err.println("MongoDB连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试方法 - 可以直接运行测试MongoDB功能
     */
    public static void main(String[] args) {
        System.out.println("开始测试MongoDB功能...");
        
        // 创建工具类实例（使用默认配置）
        StandaloneMongoUtil mongoUtil = new StandaloneMongoUtil();
        
        try {
            // 测试数据库连接
            boolean isConnected = mongoUtil.testConnection();
            System.out.println("数据库连接状态: " + (isConnected ? "成功" : "失败"));
            
            if (!isConnected) {
                System.out.println("数据库连接失败，请检查配置");
                return;
            }
            
            // 测试获取数据库信息
            System.out.println("\n1. 获取数据库信息...");
            mongoUtil.getDatabaseInfo();
            
            // 测试获取所有集合名
            System.out.println("\n2. 获取所有集合名...");
            List<String> collections = mongoUtil.getCollectionNames();
            System.out.println("数据库中共有 " + collections.size() + " 个集合:");
            for (String collection : collections) {
                System.out.println("  - " + collection);
            }
            
            // 测试插入文档
            System.out.println("\n3. 测试插入文档...");
            Document testDoc = new Document("name", "testUser")
                    .append("age", 25)
                    .append("email", "test@example.com");
            
            InsertOneResult insertResult = mongoUtil.insertOne("testCollection", testDoc);
            System.out.println("插入结果: " + (insertResult.wasAcknowledged() ? "成功" : "失败"));
            
            // 测试查询文档
            System.out.println("\n4. 测试查询文档...");
            List<Document> queryResults = mongoUtil.find("testCollection", Filters.eq("name", "testUser"));
            System.out.println("查询到 " + queryResults.size() + " 条记录");
            for (Document doc : queryResults) {
                System.out.println("  - " + doc.toJson());
            }
            
            // 测试更新文档
            System.out.println("\n5. 测试更新文档...");
            UpdateResult updateResult = mongoUtil.updateOne("testCollection", 
                    Filters.eq("name", "testUser"), 
                    Updates.set("age", 26));
            System.out.println("更新结果: 匹配 " + updateResult.getMatchedCount() + " 条，修改 " + updateResult.getModifiedCount() + " 条");
            
            // 测试删除文档
            System.out.println("\n6. 测试删除文档...");
            DeleteResult deleteResult = mongoUtil.deleteMany("testCollection", Filters.eq("name", "testUser"));
            System.out.println("删除结果: 删除 " + deleteResult.getDeletedCount() + " 条记录");
            
            System.out.println("\nMongoDB功能测试完成!");
            
        } catch (Exception e) {
            System.err.println("测试过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭连接
            mongoUtil.close();
        }
    }
    
    /**
     * 测试数据库连接
     * 
     * @return 连接是否成功
     */
    public boolean testConnection() {
        try {
            mongoClient.listDatabaseNames().first();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取数据库信息
     */
    public void getDatabaseInfo() {
        try {
            System.out.println("MongoDB服务器版本: " + mongoClient.getDatabase("admin")
                    .runCommand(new Document("buildInfo", 1))
                    .get("version"));
            
            MongoIterable<String> databaseNames = mongoClient.listDatabaseNames();
            System.out.println("所有数据库:");
            for (String dbName : databaseNames) {
                System.out.println("  - " + dbName);
            }
        } catch (Exception e) {
            System.err.println("获取数据库信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有集合名
     * 
     * @return 集合名列表
     */
    public List<String> getCollectionNames() {
        List<String> collectionNames = new ArrayList<>();
        try {
            MongoIterable<String> collections = database.listCollectionNames();
            for (String collectionName : collections) {
                collectionNames.add(collectionName);
            }
        } catch (Exception e) {
            System.err.println("获取集合名失败: " + e.getMessage());
        }
        return collectionNames;
    }
    
    /**
     * 创建集合
     * 
     * @param collectionName 集合名
     */
    public void createCollection(String collectionName) {
        try {
            database.createCollection(collectionName);
            System.out.println("集合 " + collectionName + " 创建成功");
        } catch (Exception e) {
            System.err.println("创建集合失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除集合
     * 
     * @param collectionName 集合名
     */
    public void dropCollection(String collectionName) {
        try {
            database.getCollection(collectionName).drop();
            System.out.println("集合 " + collectionName + " 删除成功");
        } catch (Exception e) {
            System.err.println("删除集合失败: " + e.getMessage());
        }
    }
    
    /**
     * 插入单个文档
     * 
     * @param collectionName 集合名
     * @param document 要插入的文档
     * @return 插入结果
     */
    public InsertOneResult insertOne(String collectionName, Document document) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            InsertOneResult result = collection.insertOne(document);
            System.out.println("文档插入成功，ID: " + document.getObjectId("_id"));
            return result;
        } catch (Exception e) {
            System.err.println("插入文档失败: " + e.getMessage());
            throw new RuntimeException("插入文档失败", e);
        }
    }
    
    /**
     * 批量插入文档
     * 
     * @param collectionName 集合名
     * @param documents 要插入的文档列表
     * @return 插入的文档ID列表
     */
    public List<String> insertMany(String collectionName, List<Document> documents) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.insertMany(documents);
            List<String> ids = new ArrayList<>();
            for (Document doc : documents) {
                ids.add(doc.getObjectId("_id").toString());
            }
            System.out.println("批量插入 " + documents.size() + " 条文档成功");
            return ids;
        } catch (Exception e) {
            System.err.println("批量插入文档失败: " + e.getMessage());
            throw new RuntimeException("批量插入文档失败", e);
        }
    }
    
    /**
     * 查询文档
     * 
     * @param collectionName 集合名
     * @param filter 过滤条件
     * @return 查询结果列表
     */
    public List<Document> find(String collectionName, Bson filter) {
        List<Document> results = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            MongoCursor<Document> cursor = collection.find(filter).iterator();
            while (cursor.hasNext()) {
                results.add(cursor.next());
            }
            cursor.close();
            System.out.println("查询完成，返回 " + results.size() + " 条记录");
        } catch (Exception e) {
            System.err.println("查询文档失败: " + e.getMessage());
            throw new RuntimeException("查询文档失败", e);
        }
        return results;
    }
    
    /**
     * 查询所有文档
     * 
     * @param collectionName 集合名
     * @return 查询结果列表
     */
    public List<Document> findAll(String collectionName) {
        return find(collectionName, new Document());
    }
    
    /**
     * 根据ID查询文档
     * 
     * @param collectionName 集合名
     * @param id 文档ID
     * @return 查询到的文档，未找到返回null
     */
    public Document findById(String collectionName, String id) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            return collection.find(Filters.eq("_id", id)).first();
        } catch (Exception e) {
            System.err.println("根据ID查询文档失败: " + e.getMessage());
            throw new RuntimeException("根据ID查询文档失败", e);
        }
    }
    
    /**
     * 更新单个文档
     * 
     * @param collectionName 集合名
     * @param filter 过滤条件
     * @param update 更新内容
     * @return 更新结果
     */
    public UpdateResult updateOne(String collectionName, Bson filter, Bson update) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            UpdateResult result = collection.updateOne(filter, update);
            System.out.println("更新完成，匹配 " + result.getMatchedCount() + " 条，修改 " + result.getModifiedCount() + " 条");
            return result;
        } catch (Exception e) {
            System.err.println("更新文档失败: " + e.getMessage());
            throw new RuntimeException("更新文档失败", e);
        }
    }
    
    /**
     * 批量更新文档
     * 
     * @param collectionName 集合名
     * @param filter 过滤条件
     * @param update 更新内容
     * @return 更新结果
     */
    public UpdateResult updateMany(String collectionName, Bson filter, Bson update) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            UpdateResult result = collection.updateMany(filter, update);
            System.out.println("批量更新完成，匹配 " + result.getMatchedCount() + " 条，修改 " + result.getModifiedCount() + " 条");
            return result;
        } catch (Exception e) {
            System.err.println("批量更新文档失败: " + e.getMessage());
            throw new RuntimeException("批量更新文档失败", e);
        }
    }
    
    /**
     * 删除单个文档
     * 
     * @param collectionName 集合名
     * @param filter 过滤条件
     * @return 删除结果
     */
    public DeleteResult deleteOne(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            DeleteResult result = collection.deleteOne(filter);
            System.out.println("删除完成，删除 " + result.getDeletedCount() + " 条记录");
            return result;
        } catch (Exception e) {
            System.err.println("删除文档失败: " + e.getMessage());
            throw new RuntimeException("删除文档失败", e);
        }
    }
    
    /**
     * 批量删除文档
     * 
     * @param collectionName 集合名
     * @param filter 过滤条件
     * @return 删除结果
     */
    public DeleteResult deleteMany(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            DeleteResult result = collection.deleteMany(filter);
            System.out.println("批量删除完成，删除 " + result.getDeletedCount() + " 条记录");
            return result;
        } catch (Exception e) {
            System.err.println("批量删除文档失败: " + e.getMessage());
            throw new RuntimeException("批量删除文档失败", e);
        }
    }
    
    /**
     * 统计文档数量
     * 
     * @param collectionName 集合名
     * @param filter 过滤条件
     * @return 文档数量
     */
    public long countDocuments(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            long count = collection.countDocuments(filter);
            System.out.println("集合 " + collectionName + " 中共有 " + count + " 条记录");
            return count;
        } catch (Exception e) {
            System.err.println("统计文档数量失败: " + e.getMessage());
            throw new RuntimeException("统计文档数量失败", e);
        }
    }
    
    /**
     * 关闭MongoDB连接
     */
    public void close() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("MongoDB连接已关闭");
            }
        } catch (Exception e) {
            System.err.println("关闭MongoDB连接失败: " + e.getMessage());
        }
    }
}