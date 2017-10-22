import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DownloadYoutubeVideos {

    GetYoutubeLinks getYoutubeLinks = new GetYoutubeLinks();

    File file;
    WebDriver driver;

    int delay = 1000;

    String videoLink;
    String channelName;
    String channelLink;
    String videoName;
    String thumbnailImage;

    FileWriter fw;
    BufferedWriter writer;


    public static void main(String[]args) throws Exception{
        DownloadYoutubeVideos downloadYoutubeVideos = new DownloadYoutubeVideos();
        downloadYoutubeVideos.run();
    }


    void run() throws Exception{
        getYoutubeLinks.run("megatoke",15);

        ArrayList<String> videoLinks = getYoutubeLinks.videoLinks;

        //start();
        driver = getYoutubeLinks.driver;
        int i = 0;
        while(i < videoLinks.size()){
            try {
                createFile();
                getVideoInfo(videoLinks.get(i));
                download();
                saveFile();
                i++;
            }catch (Exception e){
                System.out.println("Failed... Skipping...\n");
            }
        }

    }


    void start(){
        file = new File("lib/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());


        //change download path
        String downloadFilepath = "Z:\\Coding\\My Projects\\MegatokeVideos\\files\\videos";
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(cap);

    }

    void getVideoInfo(String link) throws Exception{
        videoLink = link;
        driver.get(videoLink);
        videoName = driver.findElement(By.xpath("//*[@id=\"eow-title\"]")).getText();
        channelName = driver.findElement(By.xpath("//*[@id=\"watch7-user-header\"]/div/a")).getText();
        channelLink = driver.findElement(By.xpath("//*[@id=\"watch7-user-header\"]/a")).getAttribute("href");
        thumbnailImage = "http://img.youtube.com/vi/" + videoLink.replace("https://www.youtube.com/watch?v=","") + "/0.jpg";

        writer.append("\n\"" + videoLink + "\"," + "\"" + videoName + "\"," + "\"" + channelName + "\"," + "\"" + channelLink + "\"," + "\"" + thumbnailImage + "\"");

        System.out.println(channelName + "\n" + videoName + "\n" + videoLink + "\n" + channelLink + "\n" + thumbnailImage);
        Thread.sleep(delay);

    }

    void download() throws Exception{
        driver.get("http://keepvid.com/");
        driver.findElement(By.xpath("//*[@id=\"url\"]")).sendKeys(videoLink);
        driver.findElement(By.xpath("//*[@id=\"download-form\"]/div/a")).click();
        Thread.sleep(1500);

        try{
            driver.findElement(By.xpath("//*[@id=\"shadow-confirm-video\"]/div/i")).click();
        }catch (Exception e){}


        try {
            String downloadUrl = driver.findElement(By.xpath("/html/body/div[4]/div/div[1]/div[2]/table/tbody/tr[2]/td[4]/a")).getAttribute("href");
            driver.get(downloadUrl);
            Thread.sleep(delay);

        }catch (Exception e){
            try{
                String downloadUrl = driver.findElement(By.xpath("/html/body/div[4]/div/div[1]/div[2]/table[1]/tbody/tr[2]/td[4]/a")).getAttribute("href");
                driver.get(downloadUrl);
                System.out.println("donload 1 failed...");
                Thread.sleep(delay);
            }catch (Exception f){
                try{
                    String downloadUrl = driver.findElement(By.xpath("/html/body/div[4]/div/div[1]/div[2]/table[1]/tbody/tr[3]/td[4]/a")).getAttribute("href");
                    driver.get(downloadUrl);
                    System.out.println("donload 2 failed...");
                    Thread.sleep(delay);
                }catch (Exception g){
                    String downloadUrl = driver.findElement(By.xpath("/html/body/div[4]/div/div[1]/div[2]/table/tbody/tr[3]/td[4]/a")).getAttribute("href");
                    driver.get(downloadUrl);
                    System.out.println("donload 3 failed...");
                    Thread.sleep(delay);
                }
            }
        }


        System.out.println("downloading...\n");
        Thread.sleep(delay);
    }

    void createFile()throws IOException{

        File file = new File("files/youtubeInfo.csv");
        fw = new FileWriter(file,true);
        writer = new BufferedWriter(fw);
    }

    void saveFile() throws IOException{
        writer.flush();
        writer.close();
    }

}
