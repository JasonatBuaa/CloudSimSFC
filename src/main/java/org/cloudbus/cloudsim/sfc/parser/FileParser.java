package org.cloudbus.cloudsim.sfc.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.cloudbus.cloudsim.sfc.parser.configGenerator.CustomPhysicalTopologyGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    private String rootPath = "SFCExampleConfig/";
    private String resourceFileName = "ResourceDesription.json";
    private String serverFunctionChainFileName = "ServerFunctionChainDescription.json";
    private String serverFunctionFileName = "ServerFunctionDescription.json";
    private List<ServerFunction> serverFunctions;
    private List<ServerFunctionChain> serverFunctionChains;
    private List<Resource> resources;
    private List<SFCWorkload> sfcWorkloads;

    public FileParser(){
        sfcWorkloads = new ArrayList<>();
    }

    public FileParser(String rootPath){
        this.rootPath = rootPath;
        sfcWorkloads = new ArrayList<>();
    }

    public void parser(){
        parserResource();
        parserServerFunction();
        parserServerFunctionChain();
        for(ServerFunctionChain serverFunctionChain : serverFunctionChains){
            sfcWorkloads.add(new SFCWorkload(serverFunctionChain));
        }

    }

    public void generate(){
        CustomPhysicalTopologyGenerator physicalTopologyGenerator = new CustomPhysicalTopologyGenerator();
        physicalTopologyGenerator.generate(resources);
        JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE &~ SerializerFeature.SortField.getMask();
        String jsonstr = JSON.toJSONString(physicalTopologyGenerator, SerializerFeature.PrettyFormat);
        jsonWrite(rootPath+ "PhysicalResource.json", jsonstr);

        workloadsCsvWriter(rootPath+ "workloads.csv");
    }

    public void parserResource(){
        String path = rootPath + resourceFileName;
        String jsonStr = jsonRead(path);
        resources = JSONObject.parseObject(jsonStr,new TypeReference<List<Resource>>(){});
    }

    public void parserServerFunctionChain(){
        String path = rootPath + serverFunctionChainFileName;
        String jsonStr = jsonRead(path);
        serverFunctionChains = JSONObject.parseObject(jsonStr, new TypeReference<List<ServerFunctionChain>>(){});

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

    public void jsonWrite(String path, String jsonStr){
        try{
            File jsonFile = new File(path);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonFile));
            writer.write(jsonStr);
            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void workloadsCsvWriter(String path){
        try{
            File file = new File(path);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            String line = "";
            writer.write("time(s),target_sfc,Ingress,Egress,latency_demand(s),Transmission1(MB),cloudlet.1(MI),Transmission2(MB),cloudlet.2(MI),Transmission3(MB),cloudlet.3(MI),Transmission4(MB),cloudlet.4(MI)");
            for(SFCWorkload sfcWorkload : sfcWorkloads){
                for(SFCRequest sfcRequest : sfcWorkload.getSfcRequestList()){
                    line =  sfcRequest.getStartTime()+ ","+
                            sfcWorkload.getTargetChainName() + "," +
                            sfcRequest.getIngress() + "," +
                            sfcRequest.getEgress() + "," +
                            sfcWorkload.getLatencyDemand() +
                            sfcRequest.requestsToString()
                            +"\n";
                    writer.write(line);
                }

            }
            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
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
        //System.out.println(serverFunctionChain.toString());
        System.out.println();

        System.out.println("====================SFCWorkload=================");
        //System.out.println(sfcWorkload.toString());
        System.out.println();
    }
}
