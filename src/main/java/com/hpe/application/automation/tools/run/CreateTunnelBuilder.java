package com.hpe.application.automation.tools.run;
import groovy.transform.Synchronized;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.xml.sax.SAXException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;

public class CreateTunnelBuilder extends Builder  {
    private PrintStream logger;
    private  String srfTunnelName;
    private AbstractBuild<?, ?> build;
    protected static final ArrayList<Process> Tunnels = new ArrayList<Process>();
    @DataBoundConstructor
    public CreateTunnelBuilder( String srfTunnelName ){

        this.srfTunnelName = srfTunnelName;
    }

    public String getSrfTunnelName() {
        return srfTunnelName;
    }

    @Synchronized
    @Override
    public CreateTunnelBuilder.DescriptorImpl getDescriptor() {
        return (CreateTunnelBuilder.DescriptorImpl) super.getDescriptor();
    }


    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        logger = listener.getLogger();
        JSONObject connectionData = RunFromSrfBuilder.GetSrfConnectionData(build, logger);
    String[] s = new String[5] ;
    URL url = new URL(connectionData.getString("server"));
    //check if tunnel exist
    String server = url.getHost() + ":443" ;

    if(server.indexOf("-ftaas") < 0)
        server= "opb-" + server;
    else
        server = server.replaceFirst("-ftaas", "-tunnel-ftaas");
    server ="\"" +"-server=wss://" + server + "/opb\"" ;
    String client = "\"" +"-client="  + connectionData.getString("app") +"\"";

    String secret ="\"" + "-secret="+ connectionData.getString("secret")+"\"";
    s[2]=secret;

    String name = "-name=" + srfTunnelName;

    String config = "\"-config="+srfTunnelName+"\"";

    URL proxyUrl ;
    try {
        proxyUrl =new URL(connectionData.getString("proxy"));
    }
        catch (MalformedURLException e){
        proxyUrl = new URL("http://"+connectionData.getString("proxy"));
    }
    String proxy = "\"" + "-http-proxy=" + proxyUrl.toString() +"\"" ;
    s[4] = proxy;
    String path =connectionData.getString("tunnel");



    ProcessBuilder pb = new ProcessBuilder(path,  config, "\"-log-level=INFO\"","\"-log=stdout\"");
    logger.println("Launching "+path + " " + config );

    Process p = pb.start();
    TunnelTracker tracker = new TunnelTracker(logger, p);
    java.lang.Thread th = new Thread(tracker, "trackeer");
        Tunnels.add(p);

    InputStream is = p.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;
    Timer t = new Timer();
    Date date = new Date();
        while(true){
        Date current = new Date();
        long diffSeconds = (current.getTime() - date.getTime())/1000;
        if(diffSeconds > 30){
            p.destroy();
            logger.println("Failed to launch "+path);
            Tunnels.remove(p);
            return false;
        }

        while ((line = br.readLine()) != null) {
            logger.println(line);
            if(line.indexOf("established at") >=0)
                break;
            Thread.sleep(100);
            diffSeconds = (current.getTime() - date.getTime())/1000;
            if(diffSeconds > 30){
                p.destroy();
                logger.println("Failed to launch "+path);
                return false;
            }
        }
        break;
    }

        th.start();




        return true;
}
    private JSONObject GetSrfConnectionData(){
        return new JSONObject();
    }
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private  String srfTunnelName;
        @DataBoundConstructor
        public DescriptorImpl(String srfTunnelName) {
            this.srfTunnelName = srfTunnelName;
        }
        public DescriptorImpl(){
            load();
        }
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }


        @Override
        public String getDisplayName() {
            return "Create Tunnel";
        }
    }
    static class TestRunData implements Serializable {
        static final long serialVersionUID=11;
        public TestRunData(JSONObject obj)
        {
            try {
                id = (String) obj.get("id");
                status = (String) obj.get("status");
                if (id == null) {
                    id = (String) obj.get("message");
                    status = "failed";
                }
                name = (String) obj.get("name");
                duration = obj.get("durationMs").toString();
            }
            catch (Exception e)
            {

            }
        }


        public void merge(TestRunData newData)
        {
            if (newData.name != null )  this.name = newData.name;
            if (newData.Start != null )  this.Start = newData.Start;
            if (newData.duration != null )  this.duration = newData.duration;
            if (newData.status != null )  this.status = newData.status;
            if (newData.TunnelName != null )  this.TunnelName = newData.TunnelName;
            if (newData.duration != null )  this.duration = newData.duration;
        }

        String id;                   // "932c6c3e-939e-4b17-a04f-1a2951481758",
        String name;                 // "Test-Test-Run",
        String Start;                // "2016-07-25T08:27:59.318Z",
        String duration;
        String status;               // "status" : "success",
        String TunnelName;              // "246fa1a7-7ed2-4203-a4e9-7ce5fbf4f800",
        int         execCount;
        String [] tags;
        String user;
        JSONObject _context;
    }


    private class TunnelTracker implements Runnable{
        static final long serialVersionUID=456;
        PrintStream logger;
        Process p;
        public TunnelTracker(PrintStream log, Process p){
            this.logger = log;
            this.p=p;
        }
        @Override
        public  void run() {
            try{
                //Read out dir output
                logger.println("In tracker!");
                InputStream is = p.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine())!= null){
                    // logger.println(line);
                }
            }
            catch (Exception e){
                logger.print(e.getMessage());
            }
            //Wait to get exit value
            try {
                int exitValue =0;
                p.waitFor();
                logger.println("\n\nExit Value is " + exitValue);
            } catch (final InterruptedException e) {
                p.destroy();
                return;
            }
        }
    }



}
