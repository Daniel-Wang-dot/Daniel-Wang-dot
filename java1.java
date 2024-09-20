package java1;

import java.util.Scanner;

public class java1{
	static int a=1;
    static String[] courses = {
        // math 0-9
    	"Algebra I", "Geometry,Honors", "Geometry,CP", "Algebra II", 
    	"PreCalculus,CP", "Honors Precalculus", "AP Precalculus", "AP Calculus AB", 
    	"AP Calculus BC", "Honors Probability & Statistics",
        
        // English 10-21
    	"English 9,CP", "English 9,Honors", "English 10,CP", "English 10,Honors", 
    	"English 11", "English 11,Honors", "English 12,CP", "English 12,Honors", 
    	"SAT English (Spring)", "AP English Language & Composition", "Essay Writing for Seniors (Fall)", 
    	"Sociology (Fall)/ Anthropology(Spring)",
    	
        // Science (22-31)
        "Biology,CP", "Chemistry,Honors", "Anatomy and Physiology", "Chemistry,CP", "AP Physics I",
        "Biology,Honors", "AP Chemistry with BiWeekly Labs (Thursday Afterschool)", "AP Biology", "Physics,CP/Honors", "Environmental Science","",
        
        // social study
        

        // P.E
      

        // art
        

        // world language 
        
    };
    static int[] requiryear = {3, 4, 3, 3, 1, 2, 1, 2};


    static int getcoursetype(int courseIndex) {
        if (courseIndex >= 0 && courseIndex <= 9) return 0; // 数学
        if (courseIndex >= 10 && courseIndex <= 21) return 1; // 英语
        if (courseIndex >= 22 && courseIndex <= 31) return 2; // 科学
        if (courseIndex >= 60 && courseIndex <= 79) return 3; // 社会学
        if (courseIndex >= 60 && courseIndex <= 60) return 4; // 经济 (假设只有一门经济课)
        if (courseIndex >= 80 && courseIndex <= 89) return 5; // 体育
        if (courseIndex >= 90 && courseIndex <= 99) return 6; // 艺术或音乐
        if (courseIndex >= 100 && courseIndex <= 109) return 7; // 世界语言
        return -1;
    }

    public static int findCourseIndex(String course) {
        for (int i = 0; i < courses.length; i++) {
            if (courses[i].equalsIgnoreCase(course)) {
                return i;
            }
        }
        return -1; 
    }
    
    public static void yearsleft(String[] takenCourses) {
        for (String course : takenCourses) {
            int index = findCourseIndex(course);
            int category = getcoursetype(index);
            if (index!=-1) {
                if(requiryear[category]>0) {
                    requiryear[category]--;
                }
                else {
                    requiryear[category]=0;
                }
            }
            else {
            	a=0;
            	return ;
            }
        }
    }


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the courses you have taken:");
        String input = scan.nextLine();
        String[] takenCourses = input.split("\\.");
        
        yearsleft(takenCourses);

        String[] subjects = {"math", "english", "sicience", "social study", "economy","PE", "art", "world language"};
        if(a==0) {
        	System.out.println("input error");
        }
        else {
        	for (int i = 0; i < requiryear.length; i++) {
                System.out.println( "you have to take " +subjects[i] + " for " +requiryear[i] + " years");
            }
        }
    }
}