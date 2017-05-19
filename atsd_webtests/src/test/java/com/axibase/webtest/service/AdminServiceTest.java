package com.axibase.webtest.service;


import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sild on 02.02.15.
 */
public class AdminServiceTest extends AtstTest {
    private static final String[] NTP_SERVERS = new String[]{"us.pool.ntp.org", "0.pool.ntp.org", "1.pool.ntp.org", "2.pool.ntp.org", "3.pool.ntp.org"};
    private static final long MAX_DIFF_TIME = 60000;
    private static final int WAIT_FOR_SERVER_RESPONSE = 5000;

    @Test
    public void checkAtsdTime() {
        try {
            Assert.assertTrue(generateAssertMessage("Time should be different not more 60 sec"), Math.abs(this.getCurrentTime() - this.getAtsdTime()) < MAX_DIFF_TIME);
        } catch (AssertionError err) {
            String filepath = AtstTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            this.saveScreenshot(filepath);
            System.out.println(err.toString());
            Assert.fail();
        }

    }

    private long getAtsdTime() {
        Assert.assertEquals(this.generateAssertMessage("Should get login page"), this.driver.getTitle(), LoginService.title);
        LoginService ls = new LoginService(AtstTest.driver);
        ls.login(AtstTest.login, AtstTest.password);
        long atsd_time = 0;
        AtstTest.driver.navigate().to(AtstTest.url + "/admin/system-information");
        Assert.assertEquals("title should be System Information", "System Information", AtstTest.driver.getTitle());
        AdminService as = new AdminService(AtstTest.driver);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");
        String atsd_date_string = as.getTime();
        try {
            Date e = dateFormat.parse(atsd_date_string);
            atsd_time = e.getTime();
        } catch (Exception var6) {
            Assert.assertTrue(generateAssertMessage("Cant parse getting date row: " + atsd_date_string), false);
        }
        return atsd_time;
    }

    private long getCurrentTime() {
        long cur_time = 0;
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(WAIT_FOR_SERVER_RESPONSE);
        try {
            client.open();
            for (String server : NTP_SERVERS) {
                try {
                    InetAddress ioe = InetAddress.getByName(server);
                    TimeInfo info = client.getTime(ioe);
                    TimeStamp destNtpTime = TimeStamp.getNtpTime(info.getReturnTime());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EE, MMM dd yyyy HH:mm:ss.SSS zzz");

                    try {
                        Date date = dateFormat.parse(destNtpTime.toUTCString());
                        cur_time = date.getTime();
                        break;
                    } catch (Exception e) {
                        Assert.assertTrue(generateAssertMessage("Cannot parse current date"), false);
                    }
                } catch (Exception e2) {
                    System.out.println("Can't get response from server: " + server + ".");
                }
            }
        } catch (SocketException se) {
            System.out.println("Can't open client session");
        }

        client.close();
        return cur_time;
    }
}
