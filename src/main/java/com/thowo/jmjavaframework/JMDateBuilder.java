package com.thowo.jmjavaframework;

import android.util.ArrayMap;

import com.thowo.jmjavaframework.lang.JMConstMessage;

import java.text.ParseException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JMDateBuilder {
    private class RemainingStrDate{
        List<String> blockStrings;
        List<Integer> blockIndices;

        public RemainingStrDate(String[] strDate,List<Integer> currentlyFoundStrDateIndices){
            this.blockStrings=new ArrayList<>();
            this.blockIndices=new ArrayList<>();
            for(int w=0;w<strDate.length;w++){
                boolean found=false;
                for(Integer e:currentlyFoundStrDateIndices){
                    if(w==e){
                        found=true;
                        break;
                    }
                }
                if(!found){
                    this.blockIndices.add(w);
                    this.blockStrings.add(strDate[w]);
                }
            }
        }
        public List<String> getStrings(){
            return this.blockStrings;
        }
        public Integer getStrDateIndexOf(int index){
            if(index>=this.blockIndices.size())return -1;
            return this.blockIndices.get(index);
        }
        public Integer getSize(){
            return this.blockIndices.size();
        }
    }

    //private final String CONST_DELIMITERS = "\\s+|,\\s*|\\.\\s*";
    private final String CONST_DELIMITERS = "-|:|\\s+|,|\\.|\\/";
    private final Integer CONST_YEAR=0;
    private final Integer CONST_MONTH=1;
    private final Integer CONST_DAY=2;
    private final Integer CONST_HOUR=3;
    private final Integer CONST_MINUTE=4;
    private final Integer CONST_SECOND=5;
    private final Integer CONST_AMPM=6;
    private final Integer CONST_FORMAT_24=0;
    private final Integer CONST_FORMAT_AM=1;
    private final Integer CONST_FORMAT_PM=2;
    public static final Integer CONST_AI_NORMAL=0;
    public static final Integer CONST_AI_PREFER_PAST=1;
    public static final Integer CONST_AI_PREFER_FUTURE=2;
    
    private String[] strDate;

    private List<Integer> currentlyFoundStrDateIndices;
    private List<Integer> strDateIndices;
    private List<Integer> validDate;
    private int amPm=0;
    private Integer dateAI=0;
    private JMDate now;


    public static JMDateBuilder create(String strDate, Integer paramAI){
        return new JMDateBuilder(strDate,paramAI);
    }
    /*public static JMDateBuilder create(String strDate){
        return new JMDateBuilder(strDate,JMDateBuilder.CONST_AI_NORMAL);
    }*/
    private JMDateBuilder(String strDate, Integer paramAI){
        this.dateAI=paramAI;
        this.initVars();
        this.strDate=JMFormatCollection.strToArray(strDate,this.CONST_DELIMITERS);
        if(this.strDate==null)return;
        int block=this.strDate.length;
        if(block==0)return;
        this.findMonthText();
        this.findAmPmText();
        this.validateIntegerDate();
        this.process(block,1);
        /*if(block==1){
            this.process1();
        }else if(block==2){
            this.process2();
        }else if(block==3){
            this.process3();
        }else if(block==4){
            //this.process4();
        }else if(block==5){
            //this.process5();
        }else if(block==6){
            //this.process6();
        }else if(block==7){
            //this.process7();
        }else{
            //this.process7plus();
        }*/
    }

    public JMDate getDate(){
        if(!this.isValidDate())return this.now;
        String dt=this.validDate.get(this.CONST_YEAR)+"-";
        dt+=JMFormatCollection.leadingZero(this.validDate.get(this.CONST_MONTH),2)+"-";
        dt+=JMFormatCollection.leadingZero(this.validDate.get(this.CONST_DAY),2)+" ";
        dt+=JMFormatCollection.leadingZero(this.validDate.get(this.CONST_HOUR),2)+":";
        dt+=JMFormatCollection.leadingZero(this.validDate.get(this.CONST_MINUTE),2)+":";
        dt+=JMFormatCollection.leadingZero(this.validDate.get(this.CONST_SECOND),2);
        try {
            return JMDate.create(dt);
        } catch (ParseException e) {
            return this.now;
        }
    }

    private void findDate(){
        if(strDate.length==0)return;
        if(this.isValidDate())return;
        RemainingStrDate rem=this.getRemainingStrDate();
        if(this.strDate.length==1){
            if(this.monthDefined())return;
            if(this.yearDefined())return;
            if(this.dayDefined())return;
            if(this.hourDefined())return;
            if(this.minuteDefined())return;
            if(this.secondDefined())return;
            this.setYear(rem);
            return;
        }else if(this.strDate.length==2){
            if(this.monthDefined()){
                if(this.dayDefined() || this.yearDefined())return;

            }

        }
    }

    private Integer theBestCandidate(List<Integer> indices,List<Integer> nearTos,List<Integer> farFroms){
        if(indices==null)return -1;
        if(indices.size()==0)return -1;
        List<Integer> tmp=new ArrayList<>();
        for(Integer index:indices){
            if(index!=-1)tmp.add(index);
        }
        indices=tmp;
        Integer nearTosSize=0;
        Integer farFromsSize=0;
        if(nearTos!=null){
            nearTosSize=nearTos.size();
            tmp=new ArrayList<>();
            for(Integer index:nearTos){
                if(index!=-1)tmp.add(index);
            }
            nearTos=tmp;
        }else{
            nearTos=new ArrayList<>();
        }
        if(farFroms!=null){
            farFromsSize=farFroms.size();
            tmp=new ArrayList<>();
            for(Integer index:farFroms){
                if(index!=-1)tmp.add(index);
            }
            farFroms=tmp;
        }else{
            farFroms=new ArrayList<>();
        }
        nearTosSize=nearTos.size();
        farFromsSize=farFroms.size();

        List<Integer> newIndices=new ArrayList<>();
        //List<Integer> nearDistances=new ArrayList<>();
        //List<Integer> farDistances=new ArrayList<>();
        List<Double> aiPoints=new ArrayList<>();

        if(nearTosSize>0){
            for(Integer index:indices){
                if(farFromsSize>0){
                    for(Integer nearTo:nearTos){
                        for(Integer farFrom:farFroms){
                            newIndices.add(index);
                            Integer nD=Math.abs(index-nearTo);
                            Integer fD=Math.abs(farFrom-index);
                            aiPoints.add(1.0/nD+fD);
                        }
                    }
                }else{
                    for(Integer nearTo:nearTos){
                        newIndices.add(index);
                        Integer nD=Math.abs(index-nearTo);
                        Integer fD=0;
                        aiPoints.add(1.0/nD+fD);
                    }
                }
            }
        }else{
            if(farFromsSize>0){
                for(Integer index:indices){
                    for(Integer farFrom:farFroms){
                        newIndices.add(index);
                        Integer nD=Integer.MAX_VALUE;
                        Integer fD=Math.abs(farFrom-index);
                        aiPoints.add(1.0/nD+fD);
                    }
                }
            }else{
                for(Integer index:indices){
                    newIndices.add(index);
                    Integer nD=Integer.MAX_VALUE;
                    Integer fD=0;
                    aiPoints.add(1.0/nD+fD);
                }
            }
        }

        Double maxPoint=Double.MIN_VALUE;
        Integer ret=-1;
        for(Integer i=0;i<newIndices.size();i++){
            if(aiPoints.get(i)>maxPoint){
                maxPoint=aiPoints.get(i);
                ret=newIndices.get(i);
            }
        }
        return ret;
    }
    private int process(int block, int ymdhms){
        if(this.isValidDate())return 1;
        if(this.getRemainingStrDate().getSize()==0){
            if(!this.monthDefined()){
                if(this.yearDefined()){
                    this.fillDateFirst();
                }else{
                    this.fillDateNow();
                }
            }
            this.fillDateTimeFirst();
            return 1;
        }
        if(ymdhms==this.CONST_MONTH){
            //MONTH
            if(this.monthDefined()){
                this.process(block,this.CONST_DAY);//DAY
                return 1;
            }
            this.setMonth(this.getRemainingStrDate());
            if(!this.monthDefined()){
                this.fillDateNow();
                this.process(block,this.CONST_HOUR);//HOUR
                return 1;
            }else{
                this.process(block,this.CONST_DAY);//DAY
                return 1;
            }
        }else if(ymdhms==this.CONST_DAY){
            //DAY
            if(this.dayDefined()){
                this.process(block,this.CONST_YEAR);//YEAR
                return 1;
            }
            this.setDay(this.getRemainingStrDate());
            this.process(block,this.CONST_YEAR);//YEAR
            return 1;
        }else if(ymdhms==0){
            //YEAR
            if(this.yearDefined()){
                this.process(block,this.CONST_HOUR);//HOUR
                return 1;
            }
            this.setYear(this.getRemainingStrDate());
            if(!this.yearDefined()){
                this.fillDateFirst();
            }
            this.process(block,this.CONST_HOUR);//HOUR
            return 1;
        }else if(ymdhms==this.CONST_HOUR){
            //HOUR
            if(this.hourDefined()){
                this.process(block,this.CONST_MINUTE);//MINUTE
                return 1;
            }
            if(this.amPm!=this.CONST_FORMAT_24){
                this.setHour12(this.getRemainingStrDate());
                if(!this.hourDefined()){
                    this.setHour24(this.getRemainingStrDate());
                    if(!this.hourDefined()){
                        this.fillTimeNow();
                        this.process(block,-1);//UNKNOWN
                        return 1;
                    }else{
                        this.process(block,this.CONST_MINUTE);
                        return 1;
                    }
                }else{
                    this.process(block,this.CONST_MINUTE);//MINUTE
                    return 1;
                }
            }else{
                this.setHour24(this.getRemainingStrDate());
                if(!this.hourDefined()){
                    this.fillTimeFirst();
                    this.process(block,-1);//UNKNOWN
                    return 1;
                }else{
                    this.process(block,this.CONST_MINUTE);
                    return 1;
                }
            }
        }else if(ymdhms==4){
            //MINUTE
            if(this.minuteDefined()){
                this.process(block,this.CONST_SECOND);//SECOND
                return 1;
            }
            this.setMinute(this.getRemainingStrDate());
            if(!this.minuteDefined()){
                this.fillTimeFirst();
                this.process(block,-1);//UNKNOWN
                return 1;
            }else{
                this.process(block,this.CONST_SECOND);//SECOND
                return 1;
            }
        }else if(ymdhms==5){
            //SECOND
            if(this.secondDefined()){
                this.process(block,-1);//UNKNOWN
                return 1;
            }
            this.setSecond(this.getRemainingStrDate());
            this.process(block,-1);//UNKNOWN
            return 1;
        }else{
            fillDateTimeFirst();
            return 1;
        }
    }
    private void process1(){
        if(this.monthDefined()){
            this.fillDateTimeFirst();
        }else{
            if(this.amPm==this.CONST_FORMAT_24){
                this.setDay(this.getRemainingStrDate());
                if(!this.dayDefined()){
                    this.setMonth(this.getRemainingStrDate());
                    if(!this.monthDefined()){
                        this.assignYear(this.getRemainingStrDate().getStrDateIndexOf(0));
                        this.fillDateTimeFirst();
                    }else{
                        this.fillDateTimeFirst();
                    }
                }else{
                    this.fillDateNow();
                    this.fillTimeFirst();
                }
            }else{
                this.fillDateTimeNow();
            }
        }
    }
    private void process2(){
        if(this.monthDefined()){
            if(this.amPm!=this.CONST_FORMAT_24){
                this.fillDateFirst();
                this.fillTimeNow();
            }else{
                this.setDay(this.getRemainingStrDate());
                if(!this.dayDefined()){
                    this.assignYear(this.getRemainingStrDate().getStrDateIndexOf(0));
                    this.fillDateTimeFirst();
                }else{
                    this.fillDateNow();
                    this.fillTimeFirst();
                }
            }
        }else{
            if(this.amPm!=this.CONST_FORMAT_24){
                this.setHour12(this.getRemainingStrDate());
                if(!this.hourDefined()){
                    this.setHour24(this.getRemainingStrDate());
                    if(!this.hourDefined()){
                        this.setDay(this.getRemainingStrDate());
                        if(!this.dayDefined()){
                            this.setMonth(this.getRemainingStrDate());
                            if(!this.monthDefined())this.assignYear(this.getRemainingStrDate().getStrDateIndexOf(0));
                            this.fillDateTimeFirst();
                        }else{
                            this.fillDateNow();
                            this.fillTimeFirst();
                        }
                    }else{
                        this.fillDateNow();
                        this.fillTimeFirst();
                    }
                }else{
                    this.fillDateNow();
                    this.fillTimeFirst();
                }
            }else{
                this.setMonth(this.getRemainingStrDate());
                if(!this.monthDefined()){
                    this.setYear(this.getRemainingStrDate());
                    if(!this.yearDefined()){
                        this.setDay(this.getRemainingStrDate());
                        if(!this.dayDefined()){
                            this.setHour24(this.getRemainingStrDate());
                            if(!this.hourDefined()){
                                this.fillDateTimeNow();
                            }else{
                                this.setMinute(this.getRemainingStrDate());
                                this.fillDateNow();
                                this.fillTimeFirst();
                            }
                        }else{
                            this.fillDateNow();
                            this.fillTimeFirst();
                        }
                    }else{
                        this.fillDateTimeFirst();
                    }
                }else{
                    this.setDay(this.getRemainingStrDate());
                    if(!this.dayDefined()){
                        this.setYear(this.getRemainingStrDate());
                        if(!this.yearDefined()){
                            this.fillDateTimeNow();
                        }else{
                            this.fillDateTimeFirst();
                        }
                    }else{
                        this.fillDateTimeNow();
                    }
                }
            }
        }
    }
    private void process3(){
        if(this.monthDefined()){
            if(this.amPm!=this.CONST_FORMAT_24){
                this.setDay(this.getRemainingStrDate());
                if(!this.dayDefined()){
                    this.setHour12(this.getRemainingStrDate());
                    if(!this.hourDefined()){
                        this.setYear(this.getRemainingStrDate());
                        if(!yearDefined()){
                            this.setHour24(this.getRemainingStrDate());
                            this.fillDateNow();
                            this.fillTimeFirst();
                        }else{
                            this.fillDateFirst();
                            this.fillTimeNow();
                        }
                    }else{
                        this.fillDateNow();
                        this.fillTimeFirst();
                    }
                }else{
                    this.fillDateNow();
                    this.fillTimeNow();
                }
            }else{
                this.setDay(this.getRemainingStrDate());
                if(!this.dayDefined()){
                    this.setYear(this.getRemainingStrDate());
                    if(!this.yearDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateFirst();
                            this.fillTimeNow();
                        }else{
                            this.fillDateFirst();
                            this.fillTimeFirst();
                        }
                    }else{
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateFirst();
                            this.fillTimeNow();
                        }else{
                            this.fillDateFirst();
                            this.fillTimeFirst();
                        }
                    }
                }else{
                    this.setYear(this.getRemainingStrDate());
                    if(!this.yearDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateNow();
                            this.fillTimeNow();
                        }else{
                            this.fillDateNow();
                            this.fillTimeFirst();
                        }
                    }else{
                        this.fillTimeFirst();
                    }
                }
            }
        }else{
            if(this.amPm!=this.CONST_FORMAT_24){
                this.setYear(this.getRemainingStrDate());
                if(!this.yearDefined()){
                    this.setHour12(this.getRemainingStrDate());
                    if(!this.hourDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateTimeNow();
                        }else{
                            this.setMinute(this.getRemainingStrDate());
                            if(!this.minuteDefined()){
                                this.setMonth(this.getRemainingStrDate());
                                if(!this.monthDefined()){
                                    this.fillDateNow();
                                    this.fillTimeFirst();
                                }else{
                                    this.fillDateTimeFirst();
                                }
                            }else{
                                this.fillDateNow();
                                this.fillTimeFirst();
                            }
                        }
                    }else{
                        this.setMinute(this.getRemainingStrDate());
                        if(!this.minuteDefined()){
                            this.setMonth(this.getRemainingStrDate());
                            if(!this.monthDefined()){
                                this.fillDateNow();
                                this.fillTimeFirst();
                            }else{
                                this.fillDateTimeFirst();
                            }
                        }else{
                            this.fillDateNow();
                            this.fillTimeFirst();
                        }
                    }
                }else{
                    this.setMonth(this.getRemainingStrDate());
                    if(!this.monthDefined()){
                        this.setHour12(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.setHour24(this.getRemainingStrDate());
                            if(!this.hourDefined()){
                                this.fillDateTimeNow();
                            }else{
                                this.fillDateTimeFirst();
                            }
                        }else{
                            this.fillDateTimeFirst();
                        }
                    }else{
                        this.fillDateFirst();
                        this.fillTimeNow();
                    }
                }
            }else{
                this.setYear(this.getRemainingStrDate());
                if(!this.yearDefined()){
                    this.setMonth(this.getRemainingStrDate());
                    if(!this.monthDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateTimeNow();
                        }else{
                            this.setMinute(this.getRemainingStrDate());
                            if(!this.minuteDefined()){
                                this.fillDateNow();
                                this.fillTimeFirst();
                            }else{
                                this.setSecond(this.getRemainingStrDate());
                                if(!this.secondDefined()){
                                    this.fillDateNow();
                                    this.fillTimeFirst();
                                }else{
                                    this.fillDateNow();
                                }
                            }
                        }
                    }else{
                        this.setDay(this.getRemainingStrDate());
                        if(!this.dayDefined()){
                            this.setHour24(this.getRemainingStrDate());
                            if(!this.hourDefined()){
                                this.fillDateTimeFirst();
                            }else{
                                this.setMinute(this.getRemainingStrDate());
                                this.fillDateTimeFirst();
                            }
                        }else{
                            this.setHour24(this.getRemainingStrDate());
                            if(!this.hourDefined()){
                                this.fillDateTimeFirst();
                            }else{
                                this.fillDateNow();
                                this.fillTimeFirst();
                            }
                        }
                    }
                }else{
                    this.setMonth(this.getRemainingStrDate());
                    if(!this.monthDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateTimeFirst();
                        }else{
                            this.setMinute(this.getRemainingStrDate());
                            this.fillDateTimeFirst();
                        }
                    }else{
                        this.setDay(this.getRemainingStrDate());
                        if(!this.dayDefined()){
                            this.setHour24(this.getRemainingStrDate());
                            this.fillDateTimeFirst();
                        }else{
                            this.fillTimeFirst();
                        }
                    }
                }
            }
        }
    }
    /*private void process4(){
        if(this.monthDefined()){
            if(this.amPm!=this.CONST_FORMAT_24){
                this.setDay(this.getRemainingStrDate());
                if(!this.dayDefined()){
                    this.setHour12(this.getRemainingStrDate());
                    if(!this.hourDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.setYear(this.getRemainingStrDate());
                            if(!this.yearDefined()){
                                this.fillDateTimeFirst();
                            }else{
                                this.fillDateFirst();
                                this.fillTimeNow();
                            }
                        }else{
                            this.setMinute(this.getRemainingStrDate());
                            if(!this.minuteDefined()){
                                this.setYear(this.getRemainingStrDate());
                                this.fillDateTimeFirst();
                            }else{
                                this.fillDateTimeFirst();
                            }
                        }
                    }else{
                        this.setMinute(this.getRemainingStrDate());
                        if(!this.minuteDefined()){
                            this.setYear(this.getRemainingStrDate());
                            this.fillDateTimeFirst();
                        }else{
                            this.fillDateTimeFirst();
                        }
                    }
                }else{
                    this.setHour12(this.getRemainingStrDate());
                    if(!this.hourDefined()){
                        this.setYear(this.getRemainingStrDate());
                        if(!this.yearDefined()){
                            this.fillDateTimeNow();
                        }else{
                            this.fillTimeNow();
                        }
                    }else{
                        this.fillDateTimeFirst();
                    }
                }
            }else{
                this.setDay(this.getRemainingStrDate());
                if(!this.dayDefined()){
                    this.setYear(this.getRemainingStrDate());
                    if(!this.yearDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateTimeFirst();
                        }else{
                            this.setMinute(this.getRemainingStrDate());
                            if(!this.minuteDefined()){
                                this.fillDateTimeFirst();
                            }else{
                                this.setSecond(this.getRemainingStrDate());
                                this.fillDateTimeFirst();
                            }
                        }
                    }else{
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateTimeFirst();
                        }else{
                            this.setMinute(this.getRemainingStrDate());
                            this.fillDateTimeFirst();
                        }
                    }
                }else{
                    this.setYear(this.getRemainingStrDate());
                    if(!this.yearDefined()){
                        this.setHour24(this.getRemainingStrDate());
                        if(!this.hourDefined()){
                            this.fillDateTimeFirst();
                        }else{
                            this.setMinute(this.getRemainingStrDate());
                            this.fillDateTimeFirst();
                        }
                    }else{
                        this.setHour24(this.getRemainingStrDate());
                        this.fillTimeFirst();
                    }
                }
            }
        }else{
            if(this.amPm!=this.CONST_FORMAT_24){
                this.setMonth(this.getRemainingStrDate());
                if(!this.monthDefined()){

                }else{
                    this.setDay(this.getRemainingStrDate());
                    if(!this.)
                }
            }else{

            }
        }
    }*/
    private void processTmp(){
        if(this.monthDefined()){
            if(this.amPm!=this.CONST_FORMAT_24){

            }else{

            }
        }else{
            if(this.amPm!=this.CONST_FORMAT_24){

            }else{

            }
        }
    }

    private void setYear(RemainingStrDate rem){
        if(this.yearDefined())return;
        if(rem.getSize()==0){
            this.fillYear();
            return;
        }
        List<Integer> foundIndices=this.find59(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getMonthIndex());
            nearIndices.add(this.getDayIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getHourIndex());
            farIndices.add(this.getMinuteIndex());
            farIndices.add(this.getSecondIndex());
            farIndices.add(this.getAmPmIndex());

            this.assignYear(this.theBestCandidate(foundIndices,nearIndices,farIndices));
            return;
        }
        if(!this.monthDefined() && !this.dayDefined()){
            if(rem.getSize()==1){
                foundIndices=this.findMaxDayInMonthNow(rem);
                if(foundIndices.size()>0){
                    if(this.dayDefined())return;
                    List<Integer> nearIndices=new ArrayList<>();
                    nearIndices.add(this.getMonthIndex());
                    nearIndices.add(this.getYearIndex());
                    List<Integer> farIndices=new ArrayList<>();
                    farIndices.add(this.getHourIndex());
                    farIndices.add(this.getMinuteIndex());
                    farIndices.add(this.getSecondIndex());
                    farIndices.add(this.getAmPmIndex());

                    this.assignDay(this.theBestCandidate(foundIndices,nearIndices,farIndices));
                    rem=this.getRemainingStrDate();
                    this.setYear(rem);
                    return;
                }
                foundIndices=this.findMax12(rem);
                if(foundIndices.size()>0){
                    if(this.monthDefined())return;
                    List<Integer> nearIndices=new ArrayList<>();
                    nearIndices.add(this.getDayIndex());
                    nearIndices.add(this.getYearIndex());
                    List<Integer> farIndices=new ArrayList<>();
                    farIndices.add(this.getHourIndex());
                    farIndices.add(this.getMinuteIndex());
                    farIndices.add(this.getSecondIndex());
                    farIndices.add(this.getAmPmIndex());

                    this.assignMonth(this.theBestCandidate(foundIndices,nearIndices,farIndices));
                    rem=this.getRemainingStrDate();
                    this.setYear(rem);
                    return;
                }
                List<Integer> nearIndices=new ArrayList<>();
                nearIndices.add(this.getMonthIndex());
                nearIndices.add(this.getDayIndex());
                List<Integer> farIndices=new ArrayList<>();
                farIndices.add(this.getHourIndex());
                farIndices.add(this.getMinuteIndex());
                farIndices.add(this.getSecondIndex());
                farIndices.add(this.getAmPmIndex());

                this.assignYear(this.theBestCandidate(foundIndices,nearIndices,farIndices));
                return;
            }
            if(rem.getSize()==2){
                foundIndices=this.findMax12(rem);
                if(foundIndices.size()==0){
                    foundIndices=this.findMax23(rem);
                    if(foundIndices.size()>0){
                        this.fillYear();
                        this.fillMonth();
                        this.fillDay();
                        if(this.hourDefined())return;
                        List<Integer> nearIndices=new ArrayList<>();
                        nearIndices.add(this.getAmPmIndex());
                        nearIndices.add(this.getMinuteIndex());
                        nearIndices.add(this.getSecondIndex());
                        List<Integer> farIndices=new ArrayList<>();
                        farIndices.add(this.getYearIndex());
                        farIndices.add(this.getMonthIndex());
                        farIndices.add(this.getDayIndex());

                        this.assignHour(this.theBestCandidate(foundIndices,nearIndices,farIndices));
                        rem=this.getRemainingStrDate();
                        this.setMinute(rem);
                        return;
                    }
                    if(this.yearDefined())return;
                    List<Integer> nearIndices=new ArrayList<>();
                    nearIndices.add(this.getMonthIndex());
                    nearIndices.add(this.getDayIndex());
                    List<Integer> farIndices=new ArrayList<>();
                    farIndices.add(this.getHourIndex());
                    farIndices.add(this.getMinuteIndex());
                    farIndices.add(this.getSecondIndex());
                    farIndices.add(this.getAmPmIndex());

                    this.assignYear(this.theBestCandidate(foundIndices,nearIndices,farIndices));
                    return;
                }
                if(this.monthDefined())return;
                List<Integer> nearIndices=new ArrayList<>();
                nearIndices.add(this.getYearIndex());
                nearIndices.add(this.getDayIndex());
                List<Integer> farIndices=new ArrayList<>();
                farIndices.add(this.getHourIndex());
                farIndices.add(this.getMinuteIndex());
                farIndices.add(this.getSecondIndex());
                farIndices.add(this.getAmPmIndex());

                this.assignMonth(this.theBestCandidate(foundIndices,nearIndices,farIndices));
                rem=this.getRemainingStrDate();
                this.setYear(rem);
                return;
            }
            if(rem.getSize()==3){

            }
        }
        foundIndices=this.find31(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getMonthIndex());
            nearIndices.add(this.getDayIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getHourIndex());
            farIndices.add(this.getMinuteIndex());
            farIndices.add(this.getSecondIndex());
            farIndices.add(this.getAmPmIndex());

            this.assignYear(this.theBestCandidate(foundIndices,nearIndices,farIndices));
            return;
        }
        foundIndices=this.findMaxDayInMonthNow(rem);
        if(foundIndices.size()>0){
            if(this.dayDefined())return;
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getMonthIndex());
            nearIndices.add(this.getYearIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getHourIndex());
            farIndices.add(this.getMinuteIndex());
            farIndices.add(this.getSecondIndex());
            farIndices.add(this.getAmPmIndex());

            this.assignDay(this.theBestCandidate(foundIndices,nearIndices,farIndices));
            rem=this.getRemainingStrDate();
            this.setYear(rem);
            return;
        }
        List<Integer> nearIndices=new ArrayList<>();
        nearIndices.add(this.getMonthIndex());
        nearIndices.add(this.getDayIndex());
        List<Integer> farIndices=new ArrayList<>();
        farIndices.add(this.getHourIndex());
        farIndices.add(this.getMinuteIndex());
        farIndices.add(this.getSecondIndex());
        farIndices.add(this.getAmPmIndex());

        this.assignYear(this.theBestCandidate(foundIndices,nearIndices,farIndices));
    }


    private void setMonth(RemainingStrDate rem){
        if(this.monthDefined())return;
        List<Integer> foundIndices=this.findMax12(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getYearIndex());
            nearIndices.add(this.getDayIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getHourIndex());
            farIndices.add(this.getMinuteIndex());
            farIndices.add(this.getSecondIndex());
            farIndices.add(this.getAmPmIndex());
            this.assignMonth(this.theBestCandidate(foundIndices,nearIndices,farIndices));
        }

    }
    private void setDay(RemainingStrDate rem){
        if(this.dayDefined())return;
        List<Integer> foundIndices=this.findMaxDayInMonthNow(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getYearIndex());
            nearIndices.add(this.getMonthIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getHourIndex());
            farIndices.add(this.getMinuteIndex());
            farIndices.add(this.getSecondIndex());
            farIndices.add(this.getAmPmIndex());
            this.assignDay(this.theBestCandidate(foundIndices,nearIndices,farIndices));
        }
    }
    private void setHour12(RemainingStrDate rem){
        if(this.hourDefined())return;
        List<Integer> foundIndices=this.findMax12(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getYearIndex());
            nearIndices.add(this.getMonthIndex());
            nearIndices.add(this.getDayIndex());
            //nearIndices.add(this.getAmPmIndex());
            //nearIndices.add(this.getMinuteIndex());
            //nearIndices.add(this.getSecondIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getMinuteIndex());
            farIndices.add(this.getSecondIndex());
            farIndices.add(this.getAmPmIndex());
            //farIndices.add(this.getYearIndex());
            //farIndices.add(this.getMonthIndex());
            //farIndices.add(this.getDayIndex());
            this.assignHour(this.theBestCandidate(foundIndices,nearIndices,farIndices));
        }
    }
    private void setHour24(RemainingStrDate rem){
        if(this.hourDefined())return;
        List<Integer> foundIndices=this.findMax23(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getYearIndex());
            nearIndices.add(this.getMonthIndex());
            nearIndices.add(this.getDayIndex());
            //nearIndices.add(this.getAmPmIndex());
            //nearIndices.add(this.getMinuteIndex());
            //nearIndices.add(this.getSecondIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getMinuteIndex());
            farIndices.add(this.getSecondIndex());
            farIndices.add(this.getAmPmIndex());
            //farIndices.add(this.getYearIndex());
            //farIndices.add(this.getMonthIndex());
            //farIndices.add(this.getDayIndex());
            this.assignHour(this.theBestCandidate(foundIndices,nearIndices,farIndices));
        }
    }
    private void setMinute(RemainingStrDate rem){
        if(this.minuteDefined())return;
        List<Integer> foundIndices=this.findMax59(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getAmPmIndex());
            nearIndices.add(this.getHourIndex());
            nearIndices.add(this.getSecondIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getYearIndex());
            farIndices.add(this.getMonthIndex());
            farIndices.add(this.getDayIndex());
            this.assignMinute(this.theBestCandidate(foundIndices,nearIndices,farIndices));
        }
    }
    private void setSecond(RemainingStrDate rem){
        if(this.secondDefined())return;
        List<Integer> foundIndices=this.findMax59(rem);
        if(foundIndices.size()>0){
            List<Integer> nearIndices=new ArrayList<>();
            nearIndices.add(this.getAmPmIndex());
            nearIndices.add(this.getHourIndex());
            nearIndices.add(this.getMinuteIndex());
            List<Integer> farIndices=new ArrayList<>();
            farIndices.add(this.getYearIndex());
            farIndices.add(this.getMonthIndex());
            farIndices.add(this.getDayIndex());
            this.assignSecond(this.theBestCandidate(foundIndices,nearIndices,farIndices));
        }
    }

    private List<Integer> find59(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp>59)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> find31(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp>31)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> find23(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp>23)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> find12(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp>12)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> findMax12(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp<=12)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> findMax23(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp<=23)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> findMax31(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp<=31)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> findMax59(RemainingStrDate remainingStrDate){
        List<Integer> ret=new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp<=59)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }
    private List<Integer> findMaxDayInMonthNow(RemainingStrDate remainingStrDate){
        List<Integer> ret= new ArrayList<>();
        List<String> tmpDateStr=remainingStrDate.getStrings();
        JMDate dt=this.now;
        String yy=dt.getYearFull()+"";
        String mm=JMFormatCollection.leadingZero(dt.getMonth(),2);
        String dd="01";
        String tm="00:00:00";
        if(this.monthDefined())mm=JMFormatCollection.leadingZero(this.validDate.get(this.CONST_MONTH),2);
        if(this.yearDefined())yy=this.validDate.get(this.CONST_YEAR)+"";
        try {
            dt=JMDate.create(yy+"-"+mm+"-"+dd+" "+tm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Integer max=dt.getMaxDayOfMonth();
        for(int i=0;i<tmpDateStr.size();i++){
            Integer tmp=JMFormatCollection.strToInteger(tmpDateStr.get(i),-1);
            if(tmp<=max)ret.add(remainingStrDate.getStrDateIndexOf(i));
        }
        return ret;
    }


    private void initVars(){
        this.currentlyFoundStrDateIndices=new ArrayList<>();
        this.validDate=new ArrayList<>();
        this.validDate.add(-1);
        this.validDate.add(-1);
        this.validDate.add(-1);
        this.validDate.add(-1);
        this.validDate.add(-1);
        this.validDate.add(-1);
        this.strDateIndices=new ArrayList<>();
        this.strDateIndices.add(-1);
        this.strDateIndices.add(-1);
        this.strDateIndices.add(-1);
        this.strDateIndices.add(-1);
        this.strDateIndices.add(-1);
        this.strDateIndices.add(-1);
        this.strDateIndices.add(-1);
        this.now=JMDate.now();
    }
    private void findMonthText(){
        RemainingStrDate rem=this.getRemainingStrDate();
        List<String> tmpDateStr=rem.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            for(int j=1;j<=12;j++){
                String m=JMFunctions.getMessege(JMConstMessage.MSG_DATE+JMConstMessage.MSG_DATE_MONTH+JMConstMessage.MSG_DATE_TYPE_COMPLETE+JMFormatCollection.leadingZero(j, 3));
                if(tmpDateStr.get(i).length()>1){
                    if(m.toUpperCase().contains(tmpDateStr.get(i).toUpperCase())){
                        this.assignMonth(rem.getStrDateIndexOf(i),j);
                        break;
                    }else{
                        if(j==1){m="JANUARY";}
                        else if(j==2){m="FEBRUARY";}
                        else if(j==3){m="MARCH";}
                        else if(j==4){m="APRIL";}
                        else if(j==5){m="MAY";}
                        else if(j==6){m="JUNE";}
                        else if(j==7){m="JULY";}
                        else if(j==8){m="AUGUST";}
                        else if(j==9){m="SEPTEMBER";}
                        else if(j==10){m="OCTOBER";}
                        else if(j==11){m="NOVEMBER";}
                        else {m="DECEMBER";}
                        if(m.toUpperCase().contains(tmpDateStr.get(i).toUpperCase())){
                            this.assignMonth(rem.getStrDateIndexOf(i),j);
                            break;
                        }
                    }
                }
            }
        }
    }
    private void findAmPmText(){
        RemainingStrDate rem=this.getRemainingStrDate();
        List<String> tmpDateStr=rem.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            if(tmpDateStr.get(i).toUpperCase().equals("AM")){
                this.assignAmPm(rem.getStrDateIndexOf(i),this.CONST_FORMAT_AM);
                break;
            }else if(tmpDateStr.get(i).toUpperCase().equals("PM")){
                this.assignAmPm(rem.getStrDateIndexOf(i),this.CONST_FORMAT_PM);
                break;
            }
        }
    }
    private void validateIntegerDate(){
        RemainingStrDate rem=this.getRemainingStrDate();
        List<String> tmpDateStr=rem.getStrings();
        for(int i=0;i<tmpDateStr.size();i++){
            if(JMFormatCollection.strToInteger(tmpDateStr.get(i),-1)==-1){
                this.currentlyFoundStrDateIndices.add(rem.getStrDateIndexOf(i));
            }
        }
    }

    private boolean yearDefined(){
        return this.validDate.get(this.CONST_YEAR)>-1;
    }
    private boolean monthDefined(){
        return this.validDate.get(this.CONST_MONTH)>0;
    }
    private boolean dayDefined(){
        return this.validDate.get(this.CONST_DAY)>0;
    }
    private boolean hourDefined(){
        return this.validDate.get(this.CONST_HOUR)>-1;
    }
    private boolean minuteDefined(){
        return this.validDate.get(this.CONST_MINUTE)>-1;
    }
    private boolean secondDefined(){
        return this.validDate.get(this.CONST_SECOND)>-1;
    }

    private boolean yearIndexExisted(){
        return this.strDateIndices.get(this.CONST_YEAR)!=-1;
    }
    private boolean monthIndexExisted(){
        return this.strDateIndices.get(this.CONST_MONTH)!=-1;
    }
    private boolean dayIndexExisted(){
        return this.strDateIndices.get(this.CONST_DAY)!=-1;
    }
    private boolean hourIndexExisted(){
        return this.strDateIndices.get(this.CONST_HOUR)!=-1;
    }
    private boolean minuteIndexExisted(){
        return this.strDateIndices.get(this.CONST_MINUTE)!=-1;
    }
    private boolean secondIndexExisted(){
        return this.strDateIndices.get(this.CONST_SECOND)!=-1;
    }

    private boolean isValidDate(){
        if(this.validDate==null)return false;
        if(this.validDate.size()<6)return false;
        for(Integer i:this.validDate){
            if(i==-1)return false;
        }
        return true;
    }
    private Integer getYearIndex(){
        return this.strDateIndices.get(this.CONST_YEAR);
    }
    private Integer getMonthIndex(){
        return this.strDateIndices.get(this.CONST_MONTH);
    }
    private Integer getDayIndex(){
        return this.strDateIndices.get(this.CONST_DAY);
    }
    private Integer getHourIndex(){
        return this.strDateIndices.get(this.CONST_HOUR);
    }
    private Integer getMinuteIndex(){
        return this.strDateIndices.get(this.CONST_MINUTE);
    }
    private Integer getSecondIndex(){
        return this.strDateIndices.get(this.CONST_SECOND);
    }
    private Integer getAmPmIndex(){
        return this.strDateIndices.get(this.CONST_AMPM);
    }
    private Integer validYear(Integer year){
        if(year==-1)return year;
        Integer curYear=2000;
        if(this.now!=null)curYear=this.now.getYearFull();
        Integer curYearCheckPoint=curYear/100*100;
        year=Math.abs(year);
        if(year<100){
            Integer yBottom=curYearCheckPoint-100+year;
            Integer yTop=curYearCheckPoint+year;
            if(this.dateAI==CONST_AI_NORMAL){
                if(yTop-curYear<curYear-yBottom){
                    year=yTop;
                }else{
                    year=yBottom;
                }
            }else if(this.dateAI==CONST_AI_PREFER_PAST){
                year=yBottom;
            }else{
                year=yTop;
            }

        }
        return year;
    }
    private Integer validHour(Integer hour){
        if(hour==-1)return hour;
        if(this.amPm!=this.CONST_FORMAT_24){
            if(hour>12){
                this.amPm=this.CONST_FORMAT_24;
            }else{
                if(hour==0)hour=12;
                if(hour==12)hour=0;
                ///wkkkkkkkkkkkk ;)
                if(this.amPm==this.CONST_FORMAT_PM){
                    hour+=12;
                }
            }
        }
        if(hour==24)hour=0;
        return hour;
    }
    private void fillDateTimeNow(){
        if(!this.yearDefined())this.fillYear();
        if(!this.monthDefined())this.fillMonth();
        if(!this.dayDefined())this.fillDay();
        if(!this.hourDefined())this.fillHour();
        if(!this.minuteDefined())this.fillMinute();
        if(!this.secondDefined())this.fillSecond();
    }
    private void fillDateTimeFirst(){
        if(!this.yearDefined())this.fillYear();
        if(!this.monthDefined())this.fillMonth(1);
        if(!this.dayDefined())this.fillDay(1);
        if(!this.hourDefined())this.fillHour(0);
        if(!this.minuteDefined())this.fillMinute(0);
        if(!this.secondDefined())this.fillSecond(0);
    }
    private void fillDateNow(){
        if(!this.yearDefined())this.fillYear();
        if(!this.monthDefined())this.fillMonth();
        if(!this.dayDefined())this.fillDay();
    }
    private void fillDateFirst(){
        if(!this.yearDefined())this.fillYear();
        if(!this.monthDefined())this.fillMonth(1);
        if(!this.dayDefined())this.fillDay(1);
    }
    private void fillTimeNow(){
        if(!this.hourDefined())this.fillHour();
        if(!this.minuteDefined())this.fillMinute();
        if(!this.secondDefined())this.fillSecond();
    }
    private void fillTimeFirst(){
        if(!this.hourDefined())this.fillHour(0);
        if(!this.minuteDefined())this.fillMinute(0);
        if(!this.secondDefined())this.fillSecond(0);
    }
    private void fillYear(Integer year){
        if(year!=-1)year=this.validYear(year);
        this.validDate.set(0,year);
    }
    private void fillYear(){
        this.fillYear(this.now.getYearFull());
    }
    private void fillMonth(Integer month){
        this.validDate.set(1,month);
    }
    private void fillMonth(){
        this.fillMonth(this.now.getMonth());
    }
    private void fillDay(Integer day){
        this.validDate.set(2,day);
    }
    private void fillDay(){
        this.fillDay(this.now.getDayOfMonth());
    }
    private void fillHour(Integer hour){
        hour=this.validHour(hour);
        this.validDate.set(3,hour);
    }
    private void fillHour(){
        this.fillHour(JMFormatCollection.strToInteger(this.now.getHour24()));
    }
    private void fillMinute(Integer minute){
        this.validDate.set(4,minute);
    }
    private void fillMinute(){
        this.fillMinute(JMFormatCollection.strToInteger(this.now.getMinute()));
    }
    private void fillSecond(Integer second){
        this.validDate.set(5,second);
    }
    private void fillSecond(){
        this.fillSecond(JMFormatCollection.strToInteger(this.now.getSecond()));
    }

    private void assignYear(int strDateIndex){
        if(strDateIndex==-1)return;
        Integer tmp=JMFormatCollection.strToInteger(this.strDate[strDateIndex],-1);
        if(tmp!=-1)tmp=this.validYear(tmp);
        this.validDate.set(this.CONST_YEAR,tmp);
        this.strDateIndices.set(this.CONST_YEAR,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private void assignMonth(int strDateIndex, Integer month){
        Integer tmp=month;
        this.validDate.set(this.CONST_MONTH,month);
        this.strDateIndices.set(this.CONST_MONTH,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private void assignMonth(int strDateIndex){
        if(strDateIndex==-1)return;
        Integer tmp=JMFormatCollection.strToInteger(this.strDate[strDateIndex],-1);
        this.validDate.set(this.CONST_MONTH,tmp);
        this.strDateIndices.set(this.CONST_MONTH,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private void assignDay(int strDateIndex){
        if(strDateIndex==-1)return;
        Integer tmp=JMFormatCollection.strToInteger(this.strDate[strDateIndex],-1);
        this.validDate.set(this.CONST_DAY,tmp);
        this.strDateIndices.set(this.CONST_DAY,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private void assignHour(int strDateIndex){
        if(strDateIndex==-1)return;
        Integer tmp=JMFormatCollection.strToInteger(this.strDate[strDateIndex],-1);
        tmp=this.validHour(tmp);
        this.validDate.set(this.CONST_HOUR,tmp);
        this.strDateIndices.set(this.CONST_HOUR,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private void assignMinute(int strDateIndex){
        if(strDateIndex==-1)return;
        Integer tmp=JMFormatCollection.strToInteger(this.strDate[strDateIndex],-1);
        this.validDate.set(this.CONST_MINUTE,tmp);
        this.strDateIndices.set(this.CONST_MINUTE,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private void assignSecond(int strDateIndex){
        if(strDateIndex==-1)return;
        Integer tmp=JMFormatCollection.strToInteger(this.strDate[strDateIndex],-1);
        this.validDate.set(this.CONST_SECOND,tmp);
        this.strDateIndices.set(this.CONST_SECOND,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private void assignAmPm(int strDateIndex, int amPm){
        if(strDateIndex==-1)return;
        this.amPm=amPm;
        this.strDateIndices.set(this.CONST_AMPM,strDateIndex);
        this.currentlyFoundStrDateIndices.add(strDateIndex);
    }
    private RemainingStrDate getRemainingStrDate(){
        return new RemainingStrDate(this.strDate,this.currentlyFoundStrDateIndices);
    }
}
