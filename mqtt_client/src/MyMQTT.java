/*
    <one line to give the program's name and a brief idea of what it does.>
    Copyright (C) <year>  <name of author>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MyMQTT {
    private MqttClient mqttClient;
    private final String mqttClientID;
    private MemoryPersistence memoryPersistence;
    private MqttConnectOptions mqttConnectOptions;
    public MyMQTT(String clientHost, String clientID) {
        this(clientHost, clientID, null, true);
    }

    public MyMQTT(String clientHost, String clientID, MqttCallback callback, boolean cleanSession) {
//        if (clientHost == null) {
//            System.out.println("host con not null");
//        }
        mqttClientID = clientID;
        try {
            memoryPersistence = new MemoryPersistence();
            mqttClient = new MqttClient(clientHost, clientID, memoryPersistence);
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(cleanSession);
            mqttConnectOptions.setConnectionTimeout(10);
            mqttConnectOptions.setKeepAliveInterval(20);
            if (callback == null) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        System.out.println(mqttClientID + " connectionLost " + throwable);
                        reConnect();
                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                        System.out.println(mqttClientID + " messageArrived: " + mqttMessage.toString());
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                        System.out.println(mqttClientID + " deliveryComplete " + iMqttDeliveryToken);
                    }
                });
            } else {
                mqttClient.setCallback(callback);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setPassWord(String userName, String passWord) {
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(passWord.toCharArray());
    }

    public boolean connect() {
        if (mqttClient != null) {
            try {
                mqttClient.connect(mqttConnectOptions);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            return this.isConnect();
        }
        return false;
    }

    public void publishMessage(String topic, String msg) {
        if (mqttClient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage();
                message.setQos(1);
                message.setRetained(true);
                message.setPayload(msg.getBytes());
                MqttTopic mqttTopic = mqttClient.getTopic(topic);
                MqttDeliveryToken token = mqttTopic.publish(message);
                token.waitForCompletion();
            }
            catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("MQTT is not connect");
        }
    }

    public void subTopic(String[] topicFilters, int[] qos) {
        if (mqttClient.isConnected()) {
            try {
                mqttClient.subscribe(topicFilters, qos);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("MQTT is not connect");
        }
    }

    public void cleanTopic(String[] topicFilters) {
        if (null != mqttClient && !mqttClient.isConnected()) {
            try {
                mqttClient.unsubscribe(topicFilters);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("mqttClient is error");
        }
    }

    public void closeConnect() {
        if(null != memoryPersistence) {
            try {
                memoryPersistence.close();
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("memoryPersistence is null");
        }

        if(null != mqttClient) {
            if (mqttClient.isConnected()) {
                try {
                    mqttClient.disconnect();
                    mqttClient.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("mqttClient is not connect");
            }
        } else {
            System.out.println("mqttClient is null");
        }
    }

    public boolean isConnect() {
        return mqttClient.isConnected();
    }

    public void reConnect() {
        if (null != mqttClient) {
            if (!mqttClient.isConnected()) {
                if (null != mqttConnectOptions) {
                    try {
                        mqttClient.connect(mqttConnectOptions);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("mqttConnectOptions is null");
                }
            } else {
                System.out.println("mqttClient is connect");
            }
        } else {
            System.out.println("mqttClient is null");
        }
    }
}
