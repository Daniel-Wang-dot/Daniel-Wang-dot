package java1;

import java.util.*;
import java.util.Scanner;

public class java {
	   public static int Maxlength(int[] nums) {
	        if (nums.length == 0) {
	    	    return 0;
	        }

	        int[] dp = new int[nums.length];
	        int maxLen = 1;

	        for (int i = 0; i < nums.length; i++) {
	            dp[i] = 1; 
	            for (int j = 0; j < i; j++) {
	                if (nums[i] > nums[j]) {
	                    dp[i] = Math.max(dp[i], dp[j] + 1);
	                }
	            }
	            maxLen = Math.max(maxLen, dp[i]);
	        }

	        return maxLen;
	    }
	
	    public static void main(String[] args) {	
	    	System.out.println("please enter the Array:");
	    	Scanner scan=new Scanner(System.in);
	    	int[] a=new int[5];
	    	for(int i=0;i<5;i++) {
	    		a[i]=scan.nextInt();
	    	}
	    	System.out.println(Maxlength(a));
	    }
	}