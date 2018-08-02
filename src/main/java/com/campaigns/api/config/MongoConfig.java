package com.campaigns.api.config;

import com.campaigns.api.utils.Encryptor;
import com.campaigns.api.validator.CredentialValidator;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Properties;

@Configuration
@EnableMongoRepositories(basePackages = "com.campaigns.api.repository")
//@EnableMongoAuditing
public class MongoConfig extends AbstractMongoConfiguration
{
    @Value("${mongodb.dbname}")
    private String dbName;

    @Value("${mongodb.host}")
    private String host;

    @Value("${mongodb.port}")
    private int port;

    @Value("${mongodb.username}")
    private String userName;

    @Value("${mongodb.password}")
    private String password;

    @Value("${mongo.ssh.admin}")
    private String sshAdmin;

    @Value("${mongo.allow.ssh}")
    private String allowSsh;

    @Value("${mongo.ssh.password}")
    private String sshPassword;

    @Value("${mongo.ssh.host}")
    private String sshHost;

    @Value("${mongo.ssh.keyFilePath}")
    private String sshKeyFilePath;

    @Autowired
    private Encryptor encryptor;

    @Override
    protected String getDatabaseName()
    {
        return dbName;
    }

    @Override
    public Mongo mongo() throws Exception
    {
        dbName = "HajjConnect"; //encryptor.decrypt(dbName);
        host = encryptor.decrypt(host);
//        userName = encryptor.decrypt(userName);
//        password = encryptor.decrypt(password);

        tryStartSSH(encryptor, host, port);

//        return new MongoClient(new ServerAddress(host, port), Collections.singletonList(MongoCredential.createCredential(userName, dbName, password.toCharArray())));
        return new MongoClient(new ServerAddress(host, port));
    }

    private void tryStartSSH(Encryptor encryptor, String dbHost, int port)
    {
        try
        {
            if ("true".equals(allowSsh))
            {
                String admin = encryptor.decrypt(sshAdmin);
                String password = encryptor.decrypt(sshPassword);
                String host = encryptor.decrypt(sshHost);

                JSch j = new JSch();
                j.addIdentity(sshKeyFilePath, password);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                Session sshSession = j.getSession(admin, host, 22);
                sshSession.setConfig(config);
                sshSession.connect();
                sshSession.setPortForwardingL(port, dbHost, port);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public String getMappingBasePackage()
    {
        return "com.campaigns.api.model";
    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception
    {
        return new SimpleMongoDbFactory((MongoClient) mongo(), dbName);
    }

    @Override
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() throws Exception
    {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean(name = "gridFsTemplate")
    public GridFsTemplate gridFsTemplate() throws Exception
    {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    public Encryptor encryptor()
    {
        return new Encryptor();
    }

    @Bean
    public CredentialValidator credentialValidator()
    {
        return new CredentialValidator();
    }
}
