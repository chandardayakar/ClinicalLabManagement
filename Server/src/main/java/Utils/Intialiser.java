package Utils;

import storage.FileSystemStorage;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.FileWriter;

@WebListener
public class Intialiser implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            String dataPath = FileSystemStorage.getDataPath();

            File dataPathFolder = new File(dataPath);
            if(dataPathFolder.exists()){
                if(!dataPathFolder.canWrite()){
                    throw new Exception("Need write permissions on the data folder");
                }
            }else{
                throw new Exception("Folder does not exist");
            }

            File reportsFolder = new File(dataPath + File.separator + "Reports");
            if(!reportsFolder.exists()) {
                reportsFolder.mkdir();

                File allReportsFile = new File(reportsFolder.getPath() + File.separator + "allReports");
                allReportsFile.createNewFile();
                FileWriter fw = new FileWriter(allReportsFile);
                fw.write("{\n" +
                        "\"reports\":[]\n" +
                        "}");

                fw.close();

                File reportsLastSynced = new File(reportsFolder.getPath() + File.separator + "lastSynced");
                reportsLastSynced.createNewFile();
            }

            File testsFolder = new File(dataPath + File.separator + "Tests");
            if(!testsFolder.exists()) {
                testsFolder.mkdir();

                File allTestsFile = new File(testsFolder.getPath() + File.separator + "allTests");
                allTestsFile.createNewFile();
                FileWriter fw = new FileWriter(allTestsFile);
                fw.write("{\n" +
                        "\"tests\" :[]\n" +
                        "}");
                fw.close();
                File testsLastSynced = new File(testsFolder.getPath() + File.separator + "lastSynced");
                testsLastSynced.createNewFile();
            }
            File syncFolder = new File(dataPath + File.separator + "Sync");
            if(!syncFolder.exists()){
                syncFolder.mkdir();
            }

        }catch(Exception e){
            System.out.println("Unable to initialise Data path, due to  " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
