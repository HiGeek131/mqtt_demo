public class server {
    public static void main(String[] args) {
        MyThread myThread = new MyThread("task1");
        myThread.start();
    }
}

class MyThread extends Thread {
    private String threadName;
    public MyThread(String threadName) {
        this.threadName = threadName;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        System.out.println(threadName + " run");
        MyMQTT myMQTT = new MyMQTT("tcp://higeekstudio.cn:11883", "test0");
        myMQTT.setPassWord("t", "t");
        myMQTT.connect();
        while (true) {
            myMQTT.publishMessage("test/", "test message");
            try {
                MyThread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
