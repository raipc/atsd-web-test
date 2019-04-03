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


public class AdminServiceTest extends AtsdTest {
    private static final String[] NTP_SERVERS = new String[]{"us.pool.ntp.org", "0.pool.ntp.org", "1.pool.ntp.org", "2.pool.ntp.org", "3.pool.ntp.org"};
    private static final long MAX_DIFF_TIME = 60000;
    private static final int WAIT_FOR_SERVER_RESPONSE = 5000;

    @Test
    public void checkAtsdTime() {
        try {
            Assert.assertTrue(generateAssertMessage("Time should be different not more 60 sec"), Math.abs(this.getCurrentTime() - this.getAtsdTime()) < MAX_DIFF_TIME);
        } catch (AssertionError err) {
            String filepath = AtsdTest.screenshotDir + "/" +
                    this.getClass().getSimpleName() + "_" +
                    Thread.currentThread().getStackTrace()[1].getMethodName() + "_" +
                    System.currentTimeMillis() + ".png";
            screenshotSaver.saveScreenshot(filepath);
            throw err;
        }

    }

    private long getAtsdTime() {
        Assert.assertEquals(this.generateAssertMessage("Should get login page"), LoginService.title, driver.getTitle());
        LoginService loginService = new LoginService(driver);
        loginService.login(login, password);
        driver.navigate().to(url + "/admin/system-information");
        Assert.assertEquals("title should be System Information", "System Information", driver.getTitle());
        AdminService adminService = new AdminService(driver);
        String atsdDateString = adminService.getTime();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");
            Date atsdDate = dateFormat.parse(atsdDateString);
            return atsdDate.getTime();
        } catch (Exception e) {
            Assert.fail(generateAssertMessage("Cant parse getting date row: " + atsdDateString));
        }
        return 0;
    }

    private long getCurrentTime() {
        long currentTime = 0;
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(WAIT_FOR_SERVER_RESPONSE);
        try {
            client.open();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss.SSS zzz");
            for (String server : NTP_SERVERS) {
                try {
                    InetAddress ioe = InetAddress.getByName(server);
                    TimeInfo info = client.getTime(ioe);
                    TimeStamp ntpTime = TimeStamp.getNtpTime(info.getReturnTime());
                    return ntpTime.getTime();
                } catch (Exception e2) {
                    System.out.println("Can't get response from server: " + server + ".");
                }
            }
        } catch (SocketException se) {
            System.out.println("Can't open client session");
        } finally {
            client.close();
        }
        return currentTime;
    }
}
