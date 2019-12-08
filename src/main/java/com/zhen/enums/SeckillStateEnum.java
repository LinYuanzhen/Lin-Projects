package com.zhen.enums;

//使用枚举表述常量数据字段
public enum SeckillStateEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改");

    //秒杀执行结果状态
    private int state;
    //状态描述信息
    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public static SeckillStateEnum stateof(int index){
        for(SeckillStateEnum state : values()){
            if (state.getState() == index){
                return state;
            }
        }
        return null;
    }
}
