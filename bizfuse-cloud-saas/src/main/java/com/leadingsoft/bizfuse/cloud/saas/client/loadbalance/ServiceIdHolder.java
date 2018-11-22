package com.leadingsoft.bizfuse.cloud.saas.client.loadbalance;

public class ServiceIdHolder {
    private static ThreadLocal<String> currentServerType = new ThreadLocal<String>();

    public static String getServiceId() {
        return ServiceIdHolder.currentServerType.get();
    }

    public static void setServiceId(final String serviceId) {
        ServiceIdHolder.currentServerType.set(serviceId);
    }
}
