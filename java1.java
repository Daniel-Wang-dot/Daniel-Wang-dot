package java1;

import java.util.Scanner;

public class bixiu {
    static int a = 1;

    // ==================== 原有数组：75+ 门课程 ====================
    static String[] courses = {
        // math 0-13
        "Algebra I",
        "Geometry, CP",
        "Geometry, Honors",
        "Algebra II",
        "Algebra 2, Honors",
        "Pre Calculus, CP",
        "Honors Precalculus",
        "AP Precalculus",
        "Calculus",
        "AP Calculus AB",
        "AP Calculus BC",
        "AP Statistics",
        "SAT Math (Fall)/SAT Math (Spring)",
        "Honors Probability & Statistics",

        // English 14-24
        "English 9, CP",
        "English 9, Honors",
        "English 10, CP",
        "English 10, Honors",
        "English 11",
        "English 11, Honors",
        "English 12, CP",
        "English 12, Honors",
        "SAT English (Spring)",
        "AP English Language & Composition",
        "Essay Writing for Seniors (Fall)",

        // Science 25-35
        "Biology, CP",
        "Biology, Honors",
        "AP Biology",
        "Chemistry, CP",
        "Chemistry, Honors",
        "AP Chemistry",
        "Physics, CP/Honors",
        "AP Physics I",
        "Anatomy and Physiology",
        "Environmental Science",
        "Forensic Science (Fall)/ Introduction to Organic Chemistry (Spring)",

        // Social Studies 36-51
        "Cultural Studies I/ Cultural Studies II",
        "Modern World History, CP",
        "Modern World History, Honors",
        "US History, CP",
        "US History, Honors",
        "AP US History",
        "AP US Government and Politics",
        "AP Comparative Government and Politics",
        "AP European History",
        "AP Macroeconomics",
        "AP Microeconomics",
        "AP Psychology",
        "Sociology of the Future (Fall) / Global Issues (Spring)",
        "Sociology (Fall)/Anthropology(Spring)",
        "Intro to World Religions, Mythology, and Belief Systems I (Fall) / Intro to World Religions, Mythology, and Belief",
        "National & International Current Affairs (Fall) / Public Speaking (Spring)",

        // Financial/Business Literacy(52-54)
        "Financial Literacy (Fall)/ Intro to Business (Spring)",
        "Principles of Business (Fall)/ Project Management (Spring)",
        "Entrepreneurship / Marketing",

        // PE(55)
        "PE/Health (Fall) / PE/Health (Spring)",

        // art(56-58)
        "Instrumental Music I (Fall) / Instrumental Music II(Spring)",
        "Digital Visual Art (Fall) / Cultivating Creativity (Spring)",
        "Pencil and Ink Illustration (Fall) / Drawing and Painting (Spring)",

        // world language(59-65)
        "Spanish I /Arabic I /Turkish I / Chinese I / French I (Independent Study with a Supervisor)",
        "Honors Spanish I",
        "Spanish II, Honors",
        "Spanish III, Honors",
        "Spanish IV, Honors",
        "Arabic II, CP",
        "Arabic III & IV, Honors",

        // 21st Century Life and Careers(66-74)
        "Computer Programming I (Fall) / Computer Programming II (Spring)",
        "AP Computer Science A",
        "AP Computer Science Principles",
        "Web Development I (Fall)/Web Development II",
        "Cybersecurity",
        "Dynamic Programming",
        "Principles of Engineering (Fall)\nArchitectural CAD (Spring)",
        "Graphic Design - Full Year",
        "Broadcast Media Production"
    };

    // ==================== 原有9类需求(只用来“整体”减) ====================
    // 0: math, 1: english, 2: science, 3: social, 4: finance,
    // 5: PE, 6: art, 7: worldLang, 8: 21stCentury
    static int[] requiryear = {
        3, // 0 math
        4, // 1 english
        3, // 2 science  <-- "三门" 需求
        3, // 3 social   <-- "三门" 需求
        1, // 4 economy/financial
        2, // 5 PE
        1, // 6 art
        2, // 7 world language
        1  // 8 => 21st Century Life
    };

    /**
     * 新增的【功能1】与【功能2】：
     * - Social Studies要判断用户是否修过World History / US History等
     * - Science要拆分为 life/physical/third三门；并在"第二个物理科学"时特殊处理。
     */
    // ------- Science子需求 [life, physical, third], 每项默认=1 (共3门)
    static int[] scienceSubs = {1, 1, 1};
    // 统计"已经修了多少门物理科学"
    static int physicalCount = 0;

    // ------- Social子需求 [worldHistory, usHistory, third], 每项默认=1
    static int[] socialSubs = {1, 1, 1};

    // ======================================================================
    // 原有方法：getcoursetype
    // ======================================================================
    static int getcoursetype(int courseIndex) {
        if (courseIndex >= 0 && courseIndex <= 13)   return 0; // math
        if (courseIndex >= 14 && courseIndex <= 24)  return 1; // english
        if (courseIndex >= 25 && courseIndex <= 35)  return 2; // science
        if (courseIndex >= 36 && courseIndex <= 51)  return 3; // social
        if (courseIndex >= 52 && courseIndex <= 54)  return 4; // finance
        if (courseIndex == 55)                       return 5; // PE
        if (courseIndex >= 56 && courseIndex <= 58)  return 6; // art
        if (courseIndex >= 59 && courseIndex <= 65)  return 7; // world lang
        if (courseIndex >= 66 && courseIndex <= 74)  return 8; // 21st century
        return -1;
    }

    // ======================================================================
    // 原有方法：findCourseIndex
    // ======================================================================
    public static int findCourseIndex(String course) {
        for (int i = 0; i < courses.length; i++) {
            if (courses[i].equalsIgnoreCase(course)) {
                return i;
            }
        }
        return -1; 
    }

    // ======================================================================
    // 新增【判断是不是世界史/美国史/其它社科】(功能1)
    // ======================================================================
    private static boolean isWorldHistory(String c) {
        c = c.toLowerCase();
        // 如果名称包含"modern world history"或者"world history"可认为是世界史
        return c.contains("modern world history") 
               || c.contains("world history");
    }
    private static boolean isUSHistory(String c) {
        c = c.toLowerCase();
        // "us history"
        return c.contains("us history");
    }

    // ======================================================================
    // 新增【判断是不是生命科学/物理科学】(功能2)
    // ======================================================================
    private static boolean isLifeScience(String c) {
        c = c.toLowerCase();
        return c.contains("biology") 
            || c.contains("anatomy");   // etc...
    }
    private static boolean isPhysicalScience(String c) {
        c = c.toLowerCase();
        // 物理科学 => chemistry, physics, environmental ...
        return c.contains("chemistry")
            || c.contains("physics")
            || c.contains("environmental");
    }

    // ======================================================================
    // 核心方法: yearsleft
    // ======================================================================
    public static void yearsleft(String[] takenCourses) {
        for (String course : takenCourses) {
            course = course.trim();
            // 1) 若是两门特殊课 => 忽略
            if (course.equalsIgnoreCase("Juniors Only with cumulative unweighted GPA 3.75 and above") 
                || course.equalsIgnoreCase("Seniors Only Independent Online Courses with a Supervisor (Fall) / Independent Online Courses with a Supervisor (Spring)")) 
            {
                continue;
            }

            // 2) 正常课 => 找索引
            int index = findCourseIndex(course);
            int category = getcoursetype(index);
            if (index == -1 || category == -1) {
                a = 0; // 标记错误
                return;
            }

            // 正常减该大类(只要>0就减)
            if (requiryear[category] > 0) {
                requiryear[category]--;
            }
            // 不可小于0
            else {
                requiryear[category] = 0;
            }

            // +++++++++++++++++++++++++++++++++++++++++++++++++++++
            // 【功能1】(Social) & 【功能2】(Science) 的额外细分处理
            // +++++++++++++++++++++++++++++++++++++++++++++++++++++
            if (category == 2) {  // science
                processScience(course);
            }
            else if (category == 3) { // social
                processSocial(course);
            }
        }
    }

    // ============ 处理Science的子需求 (life / physical / 3rd) ============
    private static void processScience(String course) {
        if (isLifeScience(course)) {
            // 优先扣 life
            if (scienceSubs[0] > 0) {
                scienceSubs[0]--;
            }
            else {
                // life已经满足 => 可以抵扣第三门
                if (scienceSubs[2] > 0) {
                    scienceSubs[2]--;
                }
            }
        }
        else if (isPhysicalScience(course)) {
            physicalCount++;
            if (physicalCount == 1) {
                // 第一次物理 => 扣 physical
                if (scienceSubs[1] > 0) {
                    scienceSubs[1]--;
                }
                else {
                    // 如果 physical已经0了 => 看看3rd
                    if (scienceSubs[2] > 0) {
                        scienceSubs[2]--;
                    }
                }
            } else {
                // 第二次或更多 物理科学 => 只有在 life 已满足后才能抵扣3rd
                if (scienceSubs[0] == 0 && scienceSubs[1] == 0) {
                    // life, physical都=0 => 说明前两门都修了
                    if (scienceSubs[2] > 0) {
                        scienceSubs[2]--;
                    }
                }
                // 否则就不扣(或者留在remain)
            }
        }
        else {
            // 既非life也非physical => 直接当作 third
            if (scienceSubs[2] > 0) {
                scienceSubs[2]--;
            }
        }
    }

    // ============ 处理Social的子需求 (world / us / 3rd) ============
    private static void processSocial(String course) {
        if (isWorldHistory(course)) {
            // 扣 world
            if (socialSubs[0] > 0) {
                socialSubs[0]--;
            }
            else {
                // 已经满足world => 则抵第三门
                if (socialSubs[2] > 0) {
                    socialSubs[2]--;
                }
            }
        }
        else if (isUSHistory(course)) {
            // 扣 US
            if (socialSubs[1] > 0) {
                socialSubs[1]--;
            }
            else {
                // 已经满足 => 扣 third
                if (socialSubs[2] > 0) {
                    socialSubs[2]--;
                }
            }
        }
        else {
            // 其他社科 => 扣 third
            if (socialSubs[2] > 0) {
                socialSubs[2]--;
            }
        }
    }


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the courses you have taken (use '.' to separate):");
        String input = scan.nextLine();
        String[] takenCourses = input.split("\\.");

        // 1) 处理
        yearsleft(takenCourses);

        // 2) 基础检查
        if (a == 0) {
            System.out.println("input error (some course not found or out of category range)");
            return;
        }

        // 3) 输出9大类还差几年
        String[] subjects = {
            "math",
            "english",
            "science",
            "social study",
            "economy/financial",
            "PE",
            "art",
            "world language",
            "21st century life"
        };
        for (int i = 0; i < requiryear.length; i++) {
            System.out.println("You have to take " + subjects[i] + " for " + requiryear[i] + " more years");
        }

        // 4) 额外输出: Science
        //   若 scienceSubs[0..2] 未全部=0, 则提示还需(???)
        if (scienceSubs[0] > 0 || scienceSubs[1] > 0 || scienceSubs[2] > 0) {
            // 构造提示
            StringBuilder sb = new StringBuilder("Science breakdown not fully met: \n");
            if (scienceSubs[0] > 0) sb.append("  ").append(scienceSubs[0])
                .append(" year Life Science needed\n");
            if (scienceSubs[1] > 0) sb.append("  ").append(scienceSubs[1])
                .append(" year Physical Science needed\n");
            if (scienceSubs[2] > 0) sb.append("  ").append(scienceSubs[2])
                .append(" year 3rd Science needed\n");

            // 如果用户有2个物理课，但缺Life => 可能出现多种情况
            // 这里仅示例如何输出：
            System.out.println(sb.toString());
        } else {
            System.out.println("Science sub-requirements are satisfied: (1 Life + 1 Physical + 1 Third).");
        }

        // 5) 额外输出: Social
        //   同理: [0]=world, [1]=US, [2]=third
        if (socialSubs[0] > 0 || socialSubs[1] > 0 || socialSubs[2] > 0) {
            StringBuilder sb = new StringBuilder("Social Studies breakdown not fully met: \n");
            if (socialSubs[0] > 0) sb.append("  ").append(socialSubs[0])
                .append(" year World History needed\n");
            if (socialSubs[1] > 0) sb.append("  ").append(socialSubs[1])
                .append(" year US History needed\n");
            if (socialSubs[2] > 0) sb.append("  ").append(socialSubs[2])
                .append(" year other Social Studies needed\n");
            System.out.println(sb.toString());
        } else {
            System.out.println("Social Studies sub-requirements are satisfied: (1 World + 1 US + 1 Third).");
        }
    }
}
