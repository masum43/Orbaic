package com.orbaic.miner.common;

public class Constants {
    public static final String STATE_NOT_STARTED = "not_started";
    public static final String STATE_STARTED = "started";
    public static final String STATE_ON_GOING = "on_going";

    public static final String STATUS_ON = "ON";
    public static final String STATUS_OFF = "OFF";


    //mining states
    public static final int STATE_MINING_ERROR = 0;
    public static final int STATE_MINING_ON_GOING = 1;
    public static final int STATE_MINING_DATE_DIFF_SERVER = 2;
    public static final int STATE_MINING_POINTS_GIVEN = 3;
    public static final int STATE_MINING_POINTS_NOT_GIVEN = 4;
    public static final int STATE_MINING_FINISHED = 5;
    public static final int STATE_MINING_LOCATION_NOT_GRANTED= 6;
}
