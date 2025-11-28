package com.example.test_cb.utils;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

/**
 * 独立MongoDB工具类使用示例
 * 无需启动Spring Boot项目即可直接运行
 */
public class StandaloneMongoDemo {

    public static void main(String[] args) {

        // 创建工具类实例（使用默认配置）
        StandaloneMongoUtil mongoUtil = new StandaloneMongoUtil();
        // 3. 获取所有集合名
        System.out.println("\n3. 获取所有集合名...");
        List<String> collections = mongoUtil.getCollectionNames();
        System.out.println("数据库中共有 " + collections.size() + " 个集合:");
        for (String collection : collections) {
            System.out.println("  - " + collection);
        }
        String testCollection = "demoCollection";

//        // 4. 创建测试集合
//        System.out.println("\n4. 创建测试集合...");
//        mongoUtil.createCollection(testCollection);
//        // 5. 插入单个文档
//        System.out.println("\n5. 插入单个文档...");
//        Document userDoc = new Document("name", "张三23311")
//                .append("age", 25)
//                .append("email", "zhangsan@example.com")
//                .append("department", "技术部");
//        mongoUtil.insertOne(testCollection, userDoc);


        // 8. 条件查询
        System.out.println("\n8. 条件查询（技术部员工）...");
        List<Document> techUsers = mongoUtil.find(testCollection, Filters.eq("department", "技术部"));
        System.out.println("技术部有 " + techUsers.size() + " 名员工:");
        for (Document user : techUsers) {
            System.out.println("  - " + user.getString("name") + " (" + user.getInteger("age") + "岁)");
        }
        // 关闭连接
        mongoUtil.close();
    }

    public void tt(){
        System.out.println("=== 独立MongoDB工具类演示 ===");

        // 创建工具类实例（使用默认配置）
        StandaloneMongoUtil mongoUtil = new StandaloneMongoUtil();

        try {
            // 1. 测试数据库连接
            System.out.println("\n1. 测试数据库连接...");
            boolean isConnected = mongoUtil.testConnection();
            System.out.println("连接状态: " + (isConnected ? "成功" : "失败"));

            if (!isConnected) {
                System.out.println("数据库连接失败，请检查配置");
                return;
            }

            // 2. 获取数据库信息
            System.out.println("\n2. 获取数据库信息...");
            mongoUtil.getDatabaseInfo();

            // 3. 获取所有集合名
            System.out.println("\n3. 获取所有集合名...");
            List<String> collections = mongoUtil.getCollectionNames();
            System.out.println("数据库中共有 " + collections.size() + " 个集合:");
            for (String collection : collections) {
                System.out.println("  - " + collection);
            }

            // 4. 创建测试集合
            System.out.println("\n4. 创建测试集合...");
            String testCollection = "demoCollection";
            mongoUtil.createCollection(testCollection);

            // 5. 插入单个文档
            System.out.println("\n5. 插入单个文档...");
            Document userDoc = new Document("name", "张三")
                    .append("age", 25)
                    .append("email", "zhangsan@example.com")
                    .append("department", "技术部");

            mongoUtil.insertOne(testCollection, userDoc);

            // 6. 批量插入文档
            System.out.println("\n6. 批量插入文档...");
            List<Document> userDocs = Arrays.asList(
                    new Document("name", "李四")
                            .append("age", 30)
                            .append("email", "lisi@example.com")
                            .append("department", "销售部"),
                    new Document("name", "王五")
                            .append("age", 28)
                            .append("email", "wangwu@example.com")
                            .append("department", "技术部"),
                    new Document("name", "赵六")
                            .append("age", 35)
                            .append("email", "zhaoliu@example.com")
                            .append("department", "人事部")
            );

            mongoUtil.insertMany(testCollection, userDocs);

            // 7. 查询所有文档
            System.out.println("\n7. 查询所有文档...");
            List<Document> allUsers = mongoUtil.findAll(testCollection);
            System.out.println("查询到 " + allUsers.size() + " 条用户记录:");
            for (Document user : allUsers) {
                System.out.println("  - " + user.toJson());
            }

            // 8. 条件查询
            System.out.println("\n8. 条件查询（技术部员工）...");
            List<Document> techUsers = mongoUtil.find(testCollection, Filters.eq("department", "技术部"));
            System.out.println("技术部有 " + techUsers.size() + " 名员工:");
            for (Document user : techUsers) {
                System.out.println("  - " + user.getString("name") + " (" + user.getInteger("age") + "岁)");
            }

            // 9. 年龄大于等于30的员工
            System.out.println("\n9. 条件查询（年龄>=30的员工）...");
            List<Document> olderUsers = mongoUtil.find(testCollection, Filters.gte("age", 30));
            System.out.println("年龄>=30的员工有 " + olderUsers.size() + " 名:");
            for (Document user : olderUsers) {
                System.out.println("  - " + user.getString("name") + " (" + user.getInteger("age") + "岁)");
            }

            // 10. 更新文档
            System.out.println("\n10. 更新文档（张三年龄增加1岁）...");
            mongoUtil.updateOne(testCollection,
                    Filters.eq("name", "张三"),
                    Updates.inc("age", 1));

            // 验证更新结果
            Document updatedUser = mongoUtil.find(testCollection, Filters.eq("name", "张三")).get(0);
            System.out.println("更新后的张三年龄: " + updatedUser.getInteger("age"));

            // 11. 批量更新
            System.out.println("\n11. 批量更新（技术部员工年龄都增加1岁）...");
            mongoUtil.updateMany(testCollection,
                    Filters.eq("department", "技术部"),
                    Updates.inc("age", 1));

            // 验证批量更新结果
            List<Document> updatedTechUsers = mongoUtil.find(testCollection, Filters.eq("department", "技术部"));
            System.out.println("更新后的技术部员工:");
            for (Document user : updatedTechUsers) {
                System.out.println("  - " + user.getString("name") + " (" + user.getInteger("age") + "岁)");
            }

            // 12. 统计文档数量
            System.out.println("\n12. 统计文档数量...");
            long totalUsers = mongoUtil.countDocuments(testCollection, new Document());
            System.out.println("总用户数: " + totalUsers);

            long techUserCount = mongoUtil.countDocuments(testCollection, Filters.eq("department", "技术部"));
            System.out.println("技术部用户数: " + techUserCount);

            // 13. 删除文档
            System.out.println("\n13. 删除文档（删除赵六）...");
            mongoUtil.deleteOne(testCollection, Filters.eq("name", "赵六"));

            // 验证删除结果
            long remainingUsers = mongoUtil.countDocuments(testCollection, new Document());
            System.out.println("删除后的用户总数: " + remainingUsers);

            // 14. 使用自定义连接参数
            System.out.println("\n14. 使用自定义连接参数...");
            StandaloneMongoUtil customMongoUtil = new StandaloneMongoUtil("localhost", 27017, "test");
            boolean customConnection = customMongoUtil.testConnection();
            System.out.println("自定义连接: " + (customConnection ? "成功" : "失败"));
            customMongoUtil.close();

            System.out.println("\n=== 演示完成 ===");

        } catch (Exception e) {
            System.out.println("演示过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理测试数据
            try {
                mongoUtil.dropCollection("demoCollection");
                System.out.println("\n已清理测试数据");
            } catch (Exception e) {
                System.err.println("清理测试数据失败: " + e.getMessage());
            }

            // 关闭连接
            mongoUtil.close();
        }
    }

}