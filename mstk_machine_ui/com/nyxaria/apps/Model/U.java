package com.nyxaria.apps.Model;

import com.nyxaria.apps.Controller.GPIOHandler;
import com.nyxaria.apps.Controller.RFIDReader;
import com.nyxaria.apps.View.UFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class U {

    private static int historyPageIndex;
    public static HashMap<String, String> data = new HashMap<>();
    public static HashMap<String, String> meta;
    private static Frame currentFrame;
    public static UFrame frame;
    private static String currentRFID;
    private static ArrayList<HashMap<String, String>> history;
    public static boolean debugging;
    private static long timerStart;
    private static long jobTime;
    private static Frame lastFrame;
    private static boolean loadingNextPage;
    public static void init() {
        meta = ConnectionManager.getMeta();
        frame = new UFrame();

        RFIDReader.init();
        GPIOHandler.init();
    }

    public static String getErrorMessage() {
        return meta.get(data.get("error_code"));
    }

    public static void nextPage() {
        if(loadingNextPage) return;
        historyPageIndex++;
        loadingNextPage = true;
    }

    public static void previousPage() {
        if(loadingNextPage) return;
        loadingNextPage = true;
        historyPageIndex--;
    }

    public static boolean isLoading() {
        return loadingNextPage;
    }

    public static int getPageIndex() {
        return historyPageIndex;
    }


    public static String getHistory() {
        history = ConnectionManager.getHistory(currentRFID, getPageIndex());

        String htmlContent = "<html><body><table border = '1'>";

        htmlContent += "<tr><td>Date / Time</td>"  + "<td>Rate</td>" +
                "<td>Job Time</td>" + "<td>Amount</td>" + "<td>Member $$$</td>" +
                "<td>Machine $$$</td>" + "<td>Total</td></tr>";

        for(HashMap<String, String> e : history) {

            htmlContent += "<tr><td>"+e.get("datetime")+"</td>" + "<td>"+e.getOrDefault("rate", "50")+"</td><" + "<td>"+(Integer.parseInt(e.get("job_time"))/60/60 < 10 ? "0" : "") + Integer.parseInt(e.get("job_time"))/60/60 + ":" + ((Integer.parseInt(e.get("job_time"))/60)%60 < 10 ? "0" : "") + (Integer.parseInt(e.get("job_time"))/60)%60 + ":" + (Integer.parseInt(e.get("job_time")) % 60 < 10 ? "0" : "") + Integer.parseInt(e.get("job_time")) % 60+"</td>" +
                    "<td>"+getFormattedAmount(Integer.parseInt(e.get("amount")))+"</td>" + "<td>"+getFormattedAmount(Integer.parseInt(e.get("member_store")))+"</td>" + "<td>"+getFormattedAmount(Integer.parseInt(e.get("pocket_store")))+"</td>" +
                    "<td>"+getFormattedAmount(Integer.parseInt(e.get("member_store")) + Integer.parseInt(e.get("pocket_store")))+"</td></tr>";
        }

        htmlContent += "</table></body></html>";
        loadingNextPage = false;
        return htmlContent;
    }

    public static boolean isLastPage() {
        return ConnectionManager.getHistory(currentRFID, getPageIndex()+1).size() == 0; //query next page
    }

    public static String getMachine() {
        return meta.get("access_point");
    }


    public static String getMachineNumber() {
        return data.get("access_point");
    }

    public static String getName() {
        return data.get("display_name");
    }

    public static String getMemberCredit() {
        return getFormattedAmount(Integer.parseInt(data.get("member_store")));
    }

    public static String getMachineCredit() {
        return getFormattedAmount(Integer.parseInt(data.get("pocket_store")));
    }

    public static String getTotal() {
        return getFormattedAmount(Integer.parseInt(data.get("pocket_store")) + Integer.parseInt(data.get("member_store")));
    }

    public static String getFinalCost() {
        return getFormattedAmount(Integer.parseInt(data.get("amount")));
    }

    public static String getNewBalance() {
        return getFormattedAmount(Integer.parseInt(data.get("pocket_store")) + Integer.parseInt(data.get("member_store")));
    }


    public static String getFormattedAmount(int amount) {
        int cents = Math.abs(amount % 100);
        int dollars = (amount - cents) / 100;

        String camount;
        if (cents <= 9) {
            camount = "0" + cents;
        } else {
            camount = "" + cents;
        }

        return (amount<0? "-" : "") + "$" + Math.abs(dollars) + "." + camount;
    }

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void updateLabel(String type, JLabel label) {
        if(type.equals("progress")) {

            executor.scheduleAtFixedRate(() -> {
                if(currentFrame == Frame.JobInProgress && timerStart != 0) {
                    label.setText("<html>Job In Progress<br>Elapsed time: +"+getElapsedTime()+"<br>Time remaining: " + getTimeRemaining());
                }
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }
    }

    public static String getTimeRemaining() {
        long timeS = (long) ( (Double.parseDouble(getTotal().substring(1))*100.0/(data.get("member_status").equals("1") ? .8333333333 : 1.6666667))+(-System.currentTimeMillis() + timerStart)/1000);
        return (timeS/60/60 < 10 ? "0" : "") + timeS/60/60 + ":" + ((timeS/60)%60 < 10 ? "0" : "") + (timeS/60)%60 + ":" + (timeS % 60 < 10 ? "0" : "") + timeS % 60;
    }

    public static String getTimeLeft() {
        if(getTotal().startsWith("-")) return "00:00:00";
        long timeS = (long)  ((Double.parseDouble(getTotal().substring(1))*100.0/(data.get("member_status").equals("1") ? .8333333333 : 1.6666667))-(jobTime)/1000);
        return (timeS/60/60 < 10 ? "0" : "") + timeS/60/60 + ":" + ((timeS/60)%60 < 10 ? "0" : "") + (timeS/60)%60 + ":" + (timeS % 60 < 10 ? "0" : "") + timeS % 60;
    }


    public static String getTimeAvailable() {
        long timeS = (long) ((Double.parseDouble(getTotal().substring(1))*100.0/(data.get("member_status").equals("1") ? .8333333333 : 1.6666667)));
        return (timeS/60/60 < 10 ? "0" : "") + timeS/60/60 + ":" + ((timeS/60)%60 < 10 ? "0" : "") + (timeS/60)%60 + ":" + (timeS % 60 < 10 ? "0" : "") + timeS % 60;
    }


    public static String getMachineTime() {
        long timeS = Integer.parseInt(data.get("job_time"));
        return (timeS/60/60 < 10 ? "0" : "") + timeS/60/60 + ":" + ((timeS/60)%60 < 10 ? "0" : "") + (timeS/60)%60 + ":" + (timeS % 60 < 10 ? "0" : "") + timeS % 60;
    }

    public static String getElapsedTime() {
        long timeS = ((System.currentTimeMillis() - timerStart)/1000);
        return (timeS/60/60 < 10 ? "0" : "") + timeS/60/60 + ":" + ((timeS/60)%60 < 10 ? "0" : "") + (timeS/60)%60 + ":" + (timeS % 60 < 10 ? "0" : "") + timeS % 60;
    }


    public static void triggerSignout() {
        lastFrame = currentFrame == Frame.JobStarting ? Frame.JobInProgress : currentFrame;

        if(currentFrame == Frame.JobInProgress || currentFrame == Frame.JobStarting) {
            setCurrentFrame(Frame.ConfirmSignout);
        } else {
            finaliseSignout();
        }
    }


    public static void finaliseSignout() {
        setCurrentFrame(Frame.Goodbye);

        if(data != null)
            data.clear();
        if(history != null)
            history.clear();
        currentRFID = "";

        jobTime = 0;
        timerStart = 0;

        GPIOHandler.writeInterlock(GPIOHandler.LOW);

        new java.util.Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        setCurrentFrame(Frame.Scan);
                    }
                },
                4000
        );

    }

    public static void stopSignout() {
        setCurrentFrame(lastFrame);
    }

    public static void goBack() {
        setCurrentFrame(Frame.Welcome);
    }

    public static void triggerHistory() {
        history = ConnectionManager.getHistory(currentRFID, getPageIndex());

        setCurrentFrame(Frame.History);
    }


    public static void triggerCardRead(String rfid) {
        if(currentFrame != Frame.Scan) return;
        currentRFID = rfid;
        data = ConnectionManager.login(rfid);
        if(data.get("error_code").equals("x00")) {
            if(!debugging)
                GPIOHandler.writeInterlock(GPIOHandler.HIGH);
            setCurrentFrame(Frame.Welcome);
        } else {
            if(!debugging)
                GPIOHandler.writeInterlock(GPIOHandler.LOW);
            setCurrentFrame(Frame.Error);
            new java.util.Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            finaliseSignout();
                        }
                    },
                    6000
            );
        }
    }


    public static void triggerFilterPinChanged(int state) {
        if(state == 1) {
            if(currentFrame != Frame.JobStarting && currentFrame != Frame.JobInProgress) {
                timerStart = System.currentTimeMillis();

                setCurrentFrame(Frame.JobStarting);
                new java.util.Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                if(currentFrame != Frame.JobInProgress)
                                    setCurrentFrame(Frame.JobInProgress);
                            }
                        },
                        4000
                );
            }
        } else {
            if(currentFrame == Frame.JobStarting || currentFrame == Frame.JobInProgress) {

                jobTime = System.currentTimeMillis() - timerStart;
                timerStart = 0;
                data = ConnectionManager.finaliseJob(currentRFID, "" + (jobTime / 1000));

                setCurrentFrame(Frame.JobComplete);
                new java.util.Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                data = ConnectionManager.login(currentRFID);
                                setCurrentFrame(Frame.Welcome);
                            }
                        },
                        6000
                );
            }
        }
    }


    public static void triggerInService(boolean inService) {
        if(inService) {
            if(!debugging)
                GPIOHandler.writeInterlock(1);
            meta = ConnectionManager.getMeta();
            setCurrentFrame(Frame.Scan);
        } else {
            if(!debugging)
                GPIOHandler.writeInterlock(0);

            if(data != null)
                data.clear();
            if(history != null)
                history.clear();
            currentRFID = "";

            jobTime = 0;
            timerStart = 0;

            setCurrentFrame(Frame.Maintainance);
        }
    }




    public static String getJobID() {
        return data.get("id");
    }


    public static void setCurrentFrame(Frame currentFrame) {
        U.currentFrame = currentFrame;
        if(U.frame != null)
            U.frame.changeFrame(currentFrame);
    }



    public enum Frame {
        Scan, Welcome, JobStarting, JobInProgress, JobComplete,
        Goodbye, Error, ConfirmSignout, Maintainance, History
    }
}
