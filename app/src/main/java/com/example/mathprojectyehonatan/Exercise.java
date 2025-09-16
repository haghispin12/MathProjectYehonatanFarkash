package com.example.mathprojectyehonatan;

import java.util.Random;

public class Exercise {
    private int Result;
    protected int num1;
    private int num2;
    private CallBack cal1;
    public int GetRes() {
        return this.Result;
    }

    public Exercise(CallBack A1) {
            this.cal1 = A1;
        }
    public void Ranint() { //מספר רנדומלי 0 עד 20
        Random rr = new Random();
        int num1 = rr.nextInt(9);
        int num2 = rr.nextInt(20);
        Result = num1*num2;
        cal1.Showvi(num1,num2); ///
    }


    public void Ranint10() { //מספר רנדומאלי בין 0 ל9
        Random rr = new Random();
        int num1 = rr.nextInt(9);
        int num2 = rr.nextInt(9);
        Result = num1*num2;
       cal1.Showvi(num1,num2); ///
    }

    public void Ranint100() {
        Random rrr = new Random();
        int num1 = rrr.nextInt(8)+1;
        int num2 = rrr.nextInt(89)+10;
        Result = num1*num2;
        cal1.Showvi(num1,num2);
    }
}
