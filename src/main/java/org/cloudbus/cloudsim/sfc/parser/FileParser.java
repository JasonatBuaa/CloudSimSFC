package org.cloudbus.cloudsim.sfc.parser;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.*;
import java.util.List;

public class FileParser {
    private String rootPath = "SFCExampleConfig/";
    private String resourceFileName = "ResourceDesription.json";
    private String serverFunctionChainFileName = "ServerFunctionChainDescription.json";
    private String serverFunctionFileName = "ServerFunctionDescription.json";
    private List<ServerFunction> serverFunctions;
    private ServerFunctionChain serverFunctionChain;
    private List<Resource> resources;
    private SFCWorkload sfcWorkload;

    public FileParser(){

    }
    public FileParser(String rootPath){
        this.rootPath = rootPath;
    }

    public void parser(){
        parserResource();
        parserServerFunction();
        parserServerFunctionChain();
        sfcWorkload = new SFCWorkload(serverFunctionChain);
    }

    public void parserResource(){
        String path = rootPath + resourceFileName;
        String jsonStr = jsonRead(path);
        resources = JSONObject.parseObject(jsonStr,new TypeReference<List<Resource>>(){});
    }

    public void parserServerFunctionChain(){
        String path = rootPath + serverFunctionChainFileName;
        String jsonStr = jsonRead(path);
        serverFunctionChain = JSONObject.parseObject(jsonStr, new TypeReference<ServerFunctionChain>(){});

    }

    public void parserServerFunction(){
        String path = rootPath + serverFunctionFileName;
        String jsonStr = jsonRead(path);
        serverFunctions = JSONObject.parseObject(jsonStr, new TypeReference<List<ServerFunction>>(){});
    }

    public String jsonRead(String path){
        String jsonStr = "";
        try{
            File jsonFile = new File(path);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile));
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while((ch = reader.read()) != -1){
                sb.append((char)ch);
            }
            reader.close();
            jsonStr = sb.toString();

            return jsonStr;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public void print(){
        System.out.println("====================Resource=================");

        for(Resource resource : resources){
            System.out.println(resource.toString());
        }
        System.out.println();

        System.out.println("====================ServerFunction=================");
        for(ServerFunction serverFunction : serverFunctions){
            System.out.println(serverFunction.toString());
        }
        System.out.println();

        System.out.println("====================ServerFunctionChain=================");
        System.out.println(serverFunctionChain.toString());
        System.out.println();

        System.out.println("====================SFCWorkload=================");
        System.out.println(sfcWorkload.toString());
        System.out.println();
    }
}
