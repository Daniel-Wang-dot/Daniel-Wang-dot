package java1;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FinalProgramWithAllConstraintsAndSelection
 * 需求：当出现多份 GPA 最高方案或难度最低方案时，需要同时输出所有并列方案。
 */
public class version10 {

    // ===================== 1) 静态常量(可选) =====================
    private static final int MAX_PLANS = 1000; 

    /**
     * 专业方向枚举
     */
    public enum MajorDirection {
        STEM(1),
        MEDICAL(2),
        BUSINESS(3),
        SOCIAL_SCIENCE(4),
        ENVIRONMENTAL(5),
        CS_DATA(6),
        LANGUAGE_CULTURE(7),
        LAW_POLICY(8);

        int code;
        MajorDirection(int code) {
            this.code = code;
        }
        public static MajorDirection fromCode(int c) {
            for (MajorDirection d : values()) {
                if (d.code == c) return d;
            }
            return STEM;  // 默认
        }
    }

    /**
     * 课程类别
     * 新增 FREE_PERIOD 用于表示空档
     */
    public enum CourseCategory {
        ENGLISH,
        MATH,
        SCIENCE,
        SOCIAL_STUDIES,
        FINANCIAL,  // 商科、金融、经济
        PE_HEALTH,
        VPA,         // Visual & Performing Arts
        WL,          // World Language
        LIFE_CAREERS,// 技术/职业相关
        ELECTIVES,
        FREE_PERIOD   // 表示空档
    }

    /**
     * 课程实体类
     */
    static class Course {
        String name;                    
        CourseCategory category;        
        double gpa;                     
        double difficulty;              
        double relevance;               
        List<String> prerequisites;     
        int period;                     
        int gradeLevelMin;              
        int gradeLevelMax;              
        boolean isAP;                   

        public Course(String name,
                      CourseCategory category,
                      double gpa,
                      double difficulty,
                      double relevance,
                      List<String> prereq,
                      int period,
                      int gradeLevelMin,
                      int gradeLevelMax,
                      boolean isAP) {
            this.name = name;
            this.category = category;
            this.gpa = gpa;
            this.difficulty = difficulty;
            this.relevance = relevance;
            this.prerequisites = prereq;
            this.period = period;
            this.gradeLevelMin = gradeLevelMin;
            this.gradeLevelMax = gradeLevelMax;
            this.isAP = isAP;
        }
    }

    /**
     * 毕业要求（示例）
     */
    static class GraduationRequirements {
        int englishNeeded = 20;     
        int mathNeeded = 15;        
        int scienceNeeded = 15;     
        int socialNeeded = 15;      
        int financialNeeded = 5;    
        int peHealthNeeded = 10;    
        int vpaNeeded = 5;          
        int wlNeeded = 10;          
        int lifeCareersNeeded = 5;  
        int electivesNeeded = 30;   

        void updateRequirements(List<Course> completed) {
            for (Course c : completed) {
                switch(c.category) {
                    case ENGLISH:        englishNeeded -= 5;    break;
                    case MATH:           mathNeeded -= 5;       break;
                    case SCIENCE:        scienceNeeded -= 5;    break;
                    case SOCIAL_STUDIES: socialNeeded -= 5;     break;
                    case FINANCIAL:      financialNeeded -= 5;  break;
                    case PE_HEALTH:      peHealthNeeded -= 5;   break;
                    case VPA:            vpaNeeded -= 5;        break;
                    case WL:             wlNeeded -= 5;         break;
                    case LIFE_CAREERS:   lifeCareersNeeded -= 5;break;
                    case ELECTIVES:      electivesNeeded -= 5;  break;
                    default:
                        // FREE_PERIOD不影响需求
                        break;
                }
            }
            // 不得小于0
            englishNeeded    = Math.max(0, englishNeeded);
            mathNeeded       = Math.max(0, mathNeeded);
            scienceNeeded    = Math.max(0, scienceNeeded);
            socialNeeded     = Math.max(0, socialNeeded);
            financialNeeded  = Math.max(0, financialNeeded);
            peHealthNeeded   = Math.max(0, peHealthNeeded);
            vpaNeeded        = Math.max(0, vpaNeeded);
            wlNeeded         = Math.max(0, wlNeeded);
            lifeCareersNeeded= Math.max(0, lifeCareersNeeded);
            electivesNeeded  = Math.max(0, electivesNeeded);
        }

        public boolean needThisCategory(CourseCategory cat) {
            switch(cat) {
                case ENGLISH:        return englishNeeded > 0;
                case MATH:           return mathNeeded > 0;
                case SCIENCE:        return scienceNeeded > 0;
                case SOCIAL_STUDIES: return socialNeeded > 0;
                case FINANCIAL:      return financialNeeded > 0;
                case PE_HEALTH:      return peHealthNeeded > 0;
                case VPA:            return vpaNeeded > 0;
                case WL:             return wlNeeded > 0;
                case LIFE_CAREERS:   return lifeCareersNeeded > 0;
                case ELECTIVES:      return electivesNeeded > 0;
                default:             return false;
            }
        }
    }

    //=================== 课程层次表 (courseLevelMap) ===================
    private static final Map<String,Integer> courseLevelMap = new HashMap<>();
    static {
        // -- 数学 --
        courseLevelMap.put("Algebra I", 4);
        courseLevelMap.put("Geometry, CP", 5);
        courseLevelMap.put("Geometry, Honors", 5);
        courseLevelMap.put("Algebra II", 5);
        courseLevelMap.put("Algebra 2, Honors", 5);
        courseLevelMap.put("Honors Probability & Statistics", 6);
        courseLevelMap.put("Pre Calculus, CP", 6);
        courseLevelMap.put("Honors Precalculus", 6);
        courseLevelMap.put("AP Precalculus", 6);
        courseLevelMap.put("Calculus", 7);
        courseLevelMap.put("AP Calculus AB", 7);
        courseLevelMap.put("AP Calculus BC", 8);
        courseLevelMap.put("AP Statistics", 9);
        courseLevelMap.put("SAT Math (Fall)/SAT Math (Spring)", 4);

        // ========== 以下是用户给出的6类非数学课程举例: ==========

        //Biology
        courseLevelMap.put("Biology, CP", 3);
        courseLevelMap.put("Biology, Honors", 3);
        courseLevelMap.put("AP Biology", 4);

        //Chemistry
        courseLevelMap.put("Chemistry, CP", 3);
        courseLevelMap.put("Chemistry, Honors", 3);
        courseLevelMap.put("AP Chemistry", 4);

        //Physics
        courseLevelMap.put("Physics, CP/Honors", 3);
        courseLevelMap.put("AP Physics I", 4);

        //WL
        courseLevelMap.put("Spanish I /Arabic I /Turkish I / Chinese I / French I (Independent Study with a Supervisor)", 1);
        courseLevelMap.put("Honors Spanish I", 2);
        courseLevelMap.put("Spanish II, Honors", 3);
        courseLevelMap.put("Spanish III, Honors", 4);
        courseLevelMap.put("Spanish IV, Honors", 5);

        //US History
        courseLevelMap.put("US History, CP", 3);
        courseLevelMap.put("US History, Honors", 3);
        courseLevelMap.put("AP US History", 4);

        //CS
        courseLevelMap.put("Computer Programming I (Fall) / Computer Programming II (Spring)", 3);
        courseLevelMap.put("AP Computer Science A", 4);
        courseLevelMap.put("AP Computer Science Principles", 4);
    }

    //=================== 先修课映射 (prerequisitesMap) ===================
    private static Map<String,List<String>> prerequisitesMap = new HashMap<>();
    static {
        // -- 数学 --
        prerequisitesMap.put("Geometry, CP", Arrays.asList("Algebra I"));
        prerequisitesMap.put("Geometry, Honors", Arrays.asList("Algebra I"));
        prerequisitesMap.put("Algebra II", Arrays.asList("Algebra I"));
        prerequisitesMap.put("Algebra 2, Honors", Arrays.asList("Algebra I"));
        prerequisitesMap.put("Pre Calculus, CP", Arrays.asList("Algebra II","Algebra 2, Honors"));
        prerequisitesMap.put("Honors Precalculus", Arrays.asList("Algebra II","Algebra 2, Honors"));
        prerequisitesMap.put("AP Precalculus", Arrays.asList("Algebra II","Algebra 2, Honors"));
        prerequisitesMap.put("Calculus", Arrays.asList("Pre Calculus, CP","Honors Precalculus","AP Precalculus"));
        prerequisitesMap.put("AP Calculus AB", Arrays.asList("Pre Calculus, CP","Honors Precalculus","AP Precalculus"));
        prerequisitesMap.put("AP Calculus BC", Arrays.asList("AP Calculus AB"));
        prerequisitesMap.put("AP Statistics", Arrays.asList("AP Calculus BC"));
        prerequisitesMap.put("Honors Probability & Statistics", Arrays.asList("Algebra II", "Algebra 2, Honors"));

        // -- 科学 --
        prerequisitesMap.put("AP Biology", Arrays.asList("Biology, CP","Biology, Honors"));
        prerequisitesMap.put("AP Chemistry", Arrays.asList("Chemistry, CP","Chemistry, Honors"));
        prerequisitesMap.put("AP Physics I", Arrays.asList("Physics, CP/Honors"));
        prerequisitesMap.put("AP Environmental Science", Arrays.asList("Biology, CP","Chemistry, CP"));

        // -- 计算机 --
        prerequisitesMap.put("AP Computer Science A", Arrays.asList("Computer Programming I (Fall) / Computer Programming II (Spring)"));
        prerequisitesMap.put("AP Computer Science Principles", Arrays.asList("Computer Programming I (Fall) / Computer Programming II (Spring)"));

        // -- 社会科学/经济 --
        prerequisitesMap.put("AP US History", Arrays.asList("US History, CP","US History, Honors"));

        // -- 语言 --
        prerequisitesMap.put("Spanish II, Honors", Arrays.asList("Spanish I"));
        prerequisitesMap.put("Spanish III, Honors", Arrays.asList("Spanish II, Honors"));
        prerequisitesMap.put("Spanish IV, Honors", Arrays.asList("Spanish III, Honors"));
        prerequisitesMap.put("Arabic II, CP", Arrays.asList("Arabic I"));
        prerequisitesMap.put("Arabic III & IV, Honors", Arrays.asList("Arabic II, CP"));
    }

    // ==================== 新增：需要进行 CP/Honors 互斥处理的 base 集合 ====================
    private static final Set<String> CONFLICT_BASES = new HashSet<>(
        Arrays.asList("Biology", "Chemistry", "US History")
    );
    // ==========================================================================

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入学生年级(9,10,11,12):");
        int studentGrade = sc.nextInt();
        sc.nextLine();

        System.out.println("请输入已修课程名称，以分号';'分隔(如果没有已修课则留空):");
        String completedStr = sc.nextLine().trim();
        List<String> completedCourses = new ArrayList<>();
        if(!completedStr.isEmpty()) {
            completedCourses = Arrays.stream(completedStr.split(";"))
                                     .map(String::trim)
                                     .collect(Collectors.toList());
        }

        System.out.println("请输入专业方向代码(1-STEM,2-医学,3-商科,4-社会科学与人文,5-环境科学,6-计算机与数据,7-语言文化,8-法律与公共政策):");
        int dirCode = sc.nextInt();
        MajorDirection direction = MajorDirection.fromCode(dirCode);

        System.out.println("提示：10年级，11年级的gpa需要尽量高。9年级和12年级的gpa可以较低。");
        printAPRecommendation(direction);

        // 初始化全部可选课程
        List<Course> allCourses = initAllCourses();

        // 将已完成课程名称映射到对应的 Course 对象
        List<Course> completedCourseObjs = mapCoursesByName(allCourses, completedCourses);

        // 更新毕业需求
        GraduationRequirements req = new GraduationRequirements();
        req.updateRequirements(completedCourseObjs);

        // 统计已完成的 PE/Health
        long completedPECount = completedCourseObjs.stream()
                                     .filter(c -> c.category == CourseCategory.PE_HEALTH)
                                     .count();

        // 筛选：去掉已修、低层次、不符合年级、先修不满足等课程
        List<Course> filtered = filterCourses(allCourses, completedCourses, studentGrade, req);

        // 若PE/Health已修够2门，就不再推荐更多PE/Health
        filtered = filtered.stream()
                .filter(c -> {
                    if (c.category == CourseCategory.PE_HEALTH) {
                        return completedPECount < 2;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 按时段分组
        Map<Integer,List<Course>> periodMap = filtered.stream()
                .collect(Collectors.groupingBy(c -> c.period));

        // 回溯，生成全部组合
        List<List<Course>> allPlans = generateAllPossiblePlans(periodMap, studentGrade);

        // 筛选可行
        List<List<Course>> feasiblePlans = new ArrayList<>();
        for (List<Course> plan : allPlans) {
            if (isFeasible(plan, studentGrade)) {
                feasiblePlans.add(plan);
            }
        }

        if (feasiblePlans.isEmpty()) {
            System.out.println("无可行方案");
            return;
        }

        // ============ 输出三类最优方案 ============
        System.out.println("========= GPA最高方案 =========");
        printHighestGPAPlans(feasiblePlans, direction);

        System.out.println("\n========= 与专业最相关方案(逐period挑选) =========");
        printMostRelevantPlan(feasiblePlans, direction);

        System.out.println("\n========= 难度最低方案 =========");
        printEasiestPlans(feasiblePlans, direction);
    }

    //=================== 课程初始化(示例) ===================
    private static List<Course> initAllCourses() {
        List<Course> list = new ArrayList<>();
        class H {
            void add(String name,
                     double gpa,
                     double diff,
                     double rel,
                     int period,
                     int minG,
                     int maxG,
                     boolean ap) {
                CourseCategory cat = getCategoryByName(name);
                List<String> prereq = prerequisitesMap.getOrDefault(name, new ArrayList<>());
                Course c = new Course(name, cat, gpa, diff, rel, prereq, period, minG, maxG, ap);
                list.add(c);
            }
        }
        H h = new H();

        h.add("Biology, CP", 4.33, 4.5, 4.7, 1, 9, 12, false);
        h.add("Geometry, Honors", 5.00, 2.6, 2.5, 1, 9, 12, false);
        h.add("Instrumental Music I (Fall) / Instrumental Music II(Spring)", 4.00, 4.6, 2.4, 1, 9, 12, false);
        h.add("Chemistry, Honors", 5.00, 2.7, 2.6, 1, 9, 12, false);
        h.add("Modern World History, CP", 4.33, 3.5, 4.3, 1, 9, 12, false);
        h.add("Financial Literacy (Fall)/ Intro to Business (Spring)", 4.00, 4.3, 3.5, 1, 9, 12, false);
        h.add("SAT English (Spring)", 4.00, 4.9, 3.7, 1, 9, 12, false);
        h.add("SAT Math (Fall)/SAT Math (Spring)", 4.00, 4.6, 3.4, 1, 9, 12, false);
        h.add("English 11, Honors", 5.00, 4.0, 4.6, 1, 11, 11, false);
        h.add("PE/Health (Fall) / PE/Health (Spring)", 4.00, 2.9, 3.1, 1, 9, 12, false);
        h.add("Spanish IV, Honors", 5.00, 3.7, 4.7, 1, 9, 12, false);
        h.add("Arabic III & IV, Honors", 5.00, 3.9, 4.8, 1, 9, 12, false);
        h.add("Essay Writing for Seniors (Fall)", 4.00, 3.8, 4.5, 1, 12, 12, false);
        h.add("AP Statistics", 5.33, 4.8, 5.0, 1, 10, 12, true);
        h.add("Cybersecurity", 4.00, 4.5, 4.8, 1, 9, 12, false);
    
        // ============ Period 2 ============
        h.add("Algebra I", 4.00, 3.2, 3.8, 2, 9, 12, false);
        h.add("Geometry, CP", 4.33, 3.5, 4.0, 2, 9, 12, false);
        h.add("Graphic Design - Full Year", 4.00, 3.9, 4.2, 2, 9, 12, false);
        h.add("Modern World History, CP", 4.33, 3.5, 4.3, 2, 9, 12, false);
        h.add("English 10, Honors", 5.00, 4.1, 4.7, 2, 10, 10, false);
        h.add("Honors Precalculus", 5.00, 4.7, 5.0, 2, 9, 12, false);
        h.add("Spanish III, Honors", 5.00, 3.6, 4.8, 2, 9, 12, false);
        h.add("Anatomy and Physiology", 4.00, 4.5, 4.2, 2, 9, 12, false);
        h.add("Instrumental Music I (Fall) / Instrumental Music II(Spring)", 4.00, 4.6, 2.4, 2, 9, 12, false);
        h.add("National & International Current Affairs (Fall) / Public Speaking (Spring)", 4.00, 3.7, 4.0, 2, 9, 12, false);
        h.add("English 12, CP", 4.33, 4.2, 4.5, 2, 12, 12, false);
        h.add("AP US History", 5.33, 4.9, 5.0, 2, 10, 12, true);
        h.add("Broadcast Media Production", 4.00, 3.7, 4.0, 2, 9, 12, false);
        h.add("Dynamic Programming", 4.00, 4.8, 5.0, 2, 9, 12, false);
    
        // ============ Period 3 ============
        h.add("Spanish I /Arabic I /Turkish I / Chinese I / French I (Independent Study with a Supervisor)", 4.00, 3.2, 3.5, 3, 9, 12, false);
        h.add("English 9, Honors", 5.00, 3.6, 4.5, 3, 9, 9, false);
        h.add("Digital Visual Art (Fall) / Cultivating Creativity (Spring)", 4.00, 4.0, 3.7, 3, 9, 12, false);
        h.add("Instrumental Music I (Fall) / Instrumental Music II(Spring)", 4.00, 4.6, 2.4, 3, 9, 12, false);
        h.add("English 10, CP", 4.33, 3.5, 4.0, 3, 10, 10, false);
        h.add("AP Precalculus", 5.33, 4.8, 5.0, 3, 10, 12, true);
        h.add("Chemistry, CP", 4.33, 3.5, 4.7, 3, 9, 12, false);
        h.add("Web Development I (Fall)/Web Development II", 4.00, 4.6, 5.0, 3, 9, 12, false);
        h.add("Sociology of the Future (Fall) / Global Issues (Spring)", 4.00, 3.8, 4.3, 3, 9, 12, false);
        h.add("Pre Calculus, CP", 4.33, 4.0, 4.6, 3, 9, 12, false);
        h.add("National & International Current Affairs (Fall) / Public Speaking (Spring)", 4.00, 3.7, 4.0, 3, 9, 12, false);
        h.add("PE/Health (Fall) / PE/Health (Spring)", 4.00, 2.9, 3.1, 3, 9, 12, false);
        h.add("AP English Language & Composition", 5.33, 4.6, 5.0, 3, 11, 12, true);
        h.add("Calculus", 4.00, 4.3, 4.7, 3, 9, 12, false);
        h.add("AP Physics I", 5.33, 4.9, 5.0, 3, 10, 12, true);
    
        // ============ Period 4 ============
        h.add("US History, CP", 4.33, 3.7, 4.2, 4, 9, 12, false);
        h.add("Biology, Honors", 5.00, 4.2, 4.8, 4, 9, 12, false);
        h.add("PE/Health (Fall) / PE/Health (Spring)", 4.00, 2.9, 3.1, 4, 9, 12, false);
        h.add("Spanish II, Honors", 5.00, 3.8, 4.7, 4, 9, 12, false);
        h.add("Arabic II, CP", 4.33, 3.5, 4.0, 4, 9, 12, false);
        h.add("Instrumental Music I (Fall) / Instrumental Music II(Spring)", 4.00, 4.6, 2.4, 4, 9, 12, false);
        h.add("English 11", 4.00, 3.9, 4.3, 4, 11, 11, false);
        h.add("AP Computer Science A", 5.33, 4.9, 5.0, 4, 10, 12, true);
        h.add("AP Chemistry", 5.33, 5.0, 5.0, 4, 10, 12, true);
        h.add("Financial Literacy (Fall)/ Intro to Business (Spring)", 4.00, 4.3, 3.5, 4, 9, 12, false);
        h.add("Juniors Only with cumulative unweighted GPA 3.75 and above", 4.00, 4.0, 4.0, 4, 11, 11, false);
        h.add("Seniors Only Independent Online Courses with a Supervisor (Fall) / Independent Online Courses with a Supervisor (Spring)", 4.00, 3.8, 4.2, 4, 12, 12, false);
        h.add("Intro to World Religions, Mythology, and Belief Systems I (Fall) / Intro to World Religions, Mythology, and Belief", 4.00, 3.7, 4.0, 4, 9, 12, false);
        h.add("Sociology (Fall)/Anthropology(Spring)", 4.00, 4.0, 4.5, 4, 9, 12, false);
    
        // ============ Period 5 ============
        h.add("PE/Health (Fall) / PE/Health (Spring)", 4.00, 2.9, 3.1, 5, 9, 12, false);
        h.add("English 9, Honors", 5.00, 3.6, 4.5, 5, 9, 9, false);
        h.add("Pencil and Ink Illustration (Fall) / Drawing and Painting (Spring)", 4.00, 4.0, 3.5, 5, 9, 12, false);
        h.add("US History, Honors", 5.00, 4.4, 5.0, 5, 9, 12, false);
        h.add("Chemistry, CP", 4.33, 3.5, 4.7, 5, 9, 12, false);
        h.add("Computer Programming I (Fall) / Computer Programming II (Spring)", 4.00, 4.7, 5.0, 5, 9, 12, false);
        h.add("AP Biology", 5.33, 4.9, 5.0, 5, 10, 12, true);
        h.add("AP Psychology", 5.33, 4.8, 5.0, 5, 10, 12, true);
        h.add("Physics, CP/Honors", 5.00, 4.5, 4.9, 5, 9, 12, false);
        h.add("AP Comparative Government and Politics", 5.33, 5.0, 5.0, 5, 10, 12, true);
        h.add("Entrepreneurship / Marketing", 4.00, 4.2, 4.7, 5, 9, 12, false);
        h.add("Pre Calculus, CP", 4.33, 4.0, 4.6, 5, 9, 12, false);
        h.add("Calculus", 4.00, 4.3, 4.7, 5, 9, 12, false);
        h.add("AP Calculus AB", 5.33, 4.9, 5.0, 5, 10, 12, true);
        h.add("Principles of Business (Fall)/ Project Management (Spring)", 4.00, 4.3, 4.6, 5, 9, 12, false);
    
        // ============ Period 7 ============
        h.add("English 9, CP", 4.33, 3.5, 4.0, 7, 9, 9, false);
        h.add("US History, Honors", 5.00, 4.4, 5.0, 7, 9, 12, false);
        h.add("Biology, Honors", 5.00, 4.2, 4.8, 7, 9, 12, false);
        h.add("Algebra II", 4.00, 4.0, 4.3, 7, 9, 12, false);
        h.add("Modern World History, Honors", 5.00, 4.1, 4.8, 7, 9, 12, false);
        h.add("Pencil and Ink Illustration (Fall) / Drawing and Painting (Spring)", 4.00, 4.0, 3.5, 7, 9, 12, false);
        h.add("English 10, CP", 4.33, 3.5, 4.0, 7, 10, 10, false);
        h.add("AP US Government and Politics", 5.33, 4.9, 5.0, 7, 10, 12, true);
        h.add("AP Macroeconomics", 5.33, 5.0, 5.0, 7, 10, 12, true);
        h.add("AP Computer Science Principles", 5.33, 4.7, 5.0, 7, 10, 12, true);
        h.add("Principles of Engineering (Fall)\nArchitectural CAD (Spring)", 4.00, 4.5, 4.8, 7, 9, 12, false);
        h.add("Environmental Science", 4.00, 3.8, 4.6, 7, 9, 12, false);
        h.add("English 12, Honors", 5.00, 4.2, 4.8, 7, 12, 12, false);
        h.add("Honors Probability & Statistics", 5.00, 4.5, 4.6, 7, 9, 12, false);
    
        // ============ Period 8 ============
        h.add("Digital Visual Art (Fall) / Animated Thinking (Spring)", 4.00, 3.9, 3.7, 8, 9, 12, false);
        h.add("Instrumental Music I (Fall) / Instrumental Music II(Spring)", 4.00, 4.6, 2.4, 8, 9, 12, false);
        h.add("Honors Spanish I", 5.00, 3.8, 4.5, 8, 9, 12, false);
        h.add("Geometry, CP", 4.33, 3.5, 4.0, 8, 9, 12, false);
        h.add("Algebra 2, Honors", 5.00, 4.5, 4.7, 8, 9, 12, false);
        h.add("PE/Health (Fall) / PE/Health (Spring)", 4.00, 2.9, 3.1, 8, 9, 12, false);
        h.add("Cultural Studies I/ Cultural Studies II", 4.00, 3.7, 4.0, 8, 9, 12, false);
        h.add("Forensic Science (Fall)/ Introduction to Organic Chemistry (Spring)", 4.00, 4.4, 4.5, 8, 9, 12, false);
        h.add("AP European History", 5.33, 4.8, 5.0, 8, 10, 12, true);
        h.add("English 11, Honors", 5.00, 4.0, 4.6, 8, 11, 11, false);
        h.add("AP Microeconomics", 5.33, 5.0, 5.0, 8, 10, 12, true);
        h.add("AP Calculus BC", 5.33, 5.0, 5.0, 8, 10, 12, true);
        h.add("English 12, Honors", 5.00, 4.2, 4.8, 8, 12, 12, false);
        return list;

    }

    /**
     * 根据课程名称获取类别（不改）
     */
    private static CourseCategory getCategoryByName(String name) {
        if (name.contains("English 9")||name.contains("English 10")||name.contains("English 11")||name.contains("English 12")||
            name.contains("AP English Language")||name.contains("Essay Writing for Seniors")||name.contains("SAT English")) {
            return CourseCategory.ENGLISH;
        }
        if (name.contains("Algebra I")||name.contains("Algebra II")||name.contains("Algebra 2")||name.contains("Geometry")||
            name.contains("Pre Calculus")||name.contains("PreCalculus")||name.contains("AP Precalculus")||name.contains("Honors Precalculus")||
            name.contains("Calculus")||name.contains("AP Calculus")||name.contains("Statistics")||name.contains("Probability & Statistics")||name.contains("SAT Math")) {
            return CourseCategory.MATH;
        }
        if (name.contains("Biology")||name.contains("Chemistry")||name.contains("Physics")||name.contains("Anatomy and Physiology")||
            name.contains("Environmental Science")||name.contains("Forensic Science")||name.contains("Organic Chemistry")) {
            return CourseCategory.SCIENCE;
        }
        if (name.contains("World History")||name.contains("US History")||name.contains("AP US Government")||name.contains("AP US History")||
            name.contains("AP Comparative Government")||name.contains("AP European History")||name.contains("AP Macroeconomics")||
            name.contains("AP Microeconomics")||name.contains("AP Psychology")||name.contains("Sociology")||name.contains("Global Issues")||
            name.contains("Intro to World Religions")||name.contains("Mythology")||name.contains("Cultural Studies")) {
            return CourseCategory.SOCIAL_STUDIES;
        }
        if (name.contains("Financial Literacy")||name.contains("Intro to Business")||name.contains("Principles of Business")||
            name.contains("Project Management")||name.contains("Entrepreneurship")||name.contains("Marketing")) {
            return CourseCategory.FINANCIAL;
        }
        if (name.contains("PE/Health")) {
            return CourseCategory.PE_HEALTH;
        }
        if (name.contains("Instrumental Music")||name.contains("Pencil and Ink Illustration")||name.contains("Drawing and Painting")||
            name.contains("Digital Visual Art")||name.contains("Cultivating Creativity")||name.contains("Animated Thinking")) {
            return CourseCategory.VPA;
        }
        if (name.contains("Spanish")||name.contains("Arabic")||name.contains("Turkish")||name.contains("Chinese")||name.contains("French")) {
            return CourseCategory.WL;
        }
        if (name.contains("National & International Current Affairs")||name.contains("Public Speaking")||name.contains("Graphic Design")||
            name.contains("Cybersecurity")||name.contains("Web Development")||name.contains("Computer Programming")||name.contains("AP Computer Science")||
            name.contains("Dynamic Programming")||name.contains("Principles of Engineering")||name.contains("Architectural CAD")) {
            return CourseCategory.LIFE_CAREERS;
        }
        if (name.contains("Broadcast Media Production")||name.contains("Juniors Only")||name.contains("Seniors Only Independent Online Courses")) {
            return CourseCategory.ELECTIVES;
        }
        return CourseCategory.ELECTIVES;
    }

    /**
     * 将已修课程名称映射到 Course 对象（不改）
     */
    private static List<Course> mapCoursesByName(List<Course> all, List<String> names) {
        Set<String> set = new HashSet<>(names);
        List<Course> res = new ArrayList<>();
        for (Course c : all) {
            if (set.contains(c.name)) {
                res.add(c);
            }
        }
        return res;
    }

    /**
     * 额外小方法：识别非数学课6大类（不改）
     */
    private static String getNonMathCategoryBase(String courseName) {
        if (courseName.contains("Biology")) {
            return "Biology";
        }
        if (courseName.contains("Chemistry")) {
            return "Chemistry";
        }
        if (courseName.contains("Physics")) {
            return "Physics";
        }
        if (courseName.contains("Spanish") || courseName.contains("Arabic") ||
            courseName.contains("Turkish") || courseName.contains("Chinese") || courseName.contains("French")) {
            return "WL";
        }
        if (courseName.contains("US History")) {
            return "USHistory";
        }
        if (courseName.contains("Computer Programming") ||
            courseName.contains("AP Computer Science") ||
            courseName.contains("Web Development")) {
            return "CS";
        }
        return null;
    }

    /**
     * 在此方法中去掉了 “如果该类别毕业所需已满足 && electivesNeeded<=0 => continue” 的逻辑
     */
    private static List<Course> filterCourses(List<Course> all,
                                             List<String> completedNames,
                                             int grade,
                                             GraduationRequirements req) {
        Set<String> completedSet = new HashSet<>(completedNames);

        // 收集已修 base（仅限 Biology/Chemistry/US History CP/Honors）互斥
        Set<String> completedBasesForConflict = new HashSet<>();
        for (String cmpl : completedNames) {
            if (cmpl.contains("AP ")) {
                continue;
            }
            String base = getBaseCourseName(cmpl);
            if (CONFLICT_BASES.contains(base)) {
                completedBasesForConflict.add(base);
            }
        }

        // 数学课“只允许比已修更高” 记录
        Map<CourseCategory,Integer> highestMathLevelMap = new HashMap<>();
        for (String cmpl : completedNames) {
            CourseCategory cat = getCategoryByName(cmpl);
            if (cat == CourseCategory.MATH) {
                int lvl = getLevel(cmpl);
                int old = highestMathLevelMap.getOrDefault(cat, 0);
                if (lvl > old) {
                    highestMathLevelMap.put(cat, lvl);
                }
            }
        }

        // 6大非数学类别的已修最高等级
        Map<String,Integer> highestNonMathLevelMap = new HashMap<>();
        for (String cmpl : completedNames) {
            String nonMathCat = getNonMathCategoryBase(cmpl);
            if (nonMathCat != null) {
                int lvl = getLevel(cmpl);
                highestNonMathLevelMap.merge(nonMathCat, lvl, Math::max);
            }
        }

        List<Course> res = new ArrayList<>();
        for (Course c : all) {
            // 1) 已修过则跳过
            if (completedSet.contains(c.name)) continue;

            // 1.1) CP/Honors 互斥
            if (!c.isAP) {
                String base = getBaseCourseName(c.name);
                if (CONFLICT_BASES.contains(base) && completedBasesForConflict.contains(base)) {
                    continue;
                }
            }

            // 2) 年级不符
            if (grade < c.gradeLevelMin || grade > c.gradeLevelMax) continue;

            // 3) 先修不满足
            if (!arePrerequisitesMet(c, completedSet)) continue;

            // 4) 数学课 => “只允许更高”
            if (c.category == CourseCategory.MATH) {
                int cLevel = getLevel(c.name);
                int maxLvl = highestMathLevelMap.getOrDefault(c.category, 0);
                if (cLevel <= maxLvl) {
                    continue;
                }
            } 
            else {
                // 4') 这 6 大非数学类 => 过滤 <= 已修最高等级
                String nonMathCat = getNonMathCategoryBase(c.name);
                if (nonMathCat != null) {
                    int cLevel = getLevel(c.name);
                    int maxLvl = highestNonMathLevelMap.getOrDefault(nonMathCat, 0);
                    if (cLevel <= maxLvl) {
                        continue;
                    }
                }
            }

            // -- 此处原本存在的“若该类别必修已满足 && electives也够 => continue” 已被去除 --

            res.add(c);
        }
        return res;
    }

    /**
     * 根据课程名称获取level（不改）
     */
    private static int getLevel(String courseName) {
        return courseLevelMap.getOrDefault(courseName, 0);
    }

    private static boolean arePrerequisitesMet(Course course, Set<String> completedSet) {
        List<String> prereqs = prerequisitesMap.getOrDefault(course.name, new ArrayList<>());
        if (prereqs.isEmpty()) {
            return true;
        }

        boolean isMathCourse = (course.category == CourseCategory.MATH);

        // 原本的写法里，对prereqs是逐条AND：for (String pr : prereqs) {...} if(没满足) return false
        // ——> 改为只要有一条满足则可，因此写成OR:
        boolean hasAnyPrereqSatisfied = false; // 用于记录是否“至少有一条先修被满足”

        for (String pr : prereqs) {
            // pr 可能是 "Chemistry, CP" 或 "Chemistry, Honors"
            // 可能在一个字符串里用 "/" 分割多个备选，如 "Algebra II / Algebra 2, Honors"
            String[] multipleOptions = pr.split("/");
            boolean thisLineMet = false; // 该行（先修项）是否被满足

            for (String singleOpt : multipleOptions) {
                singleOpt = singleOpt.trim();

                if (isMathCourse) {
                    // 数学课 => 同类别 + level >=
                    int needLvl = getLevel(singleOpt);
                    CourseCategory needCat = getCategoryByName(singleOpt);
                    for (String comp : completedSet) {
                        int compLvl = getLevel(comp);
                        CourseCategory compCat = getCategoryByName(comp);
                        if (compCat == needCat && compLvl >= needLvl) {
                            // 匹配到即可
                            thisLineMet = true;
                            break;
                        }
                    }
                } else {
                    // 非数学课 => 课程名称精确匹配
                    for (String comp : completedSet) {
                        if (comp.trim().equalsIgnoreCase(singleOpt)) {
                            thisLineMet = true;
                            break;
                        }
                    }
                }

                // 如果某个singleOpt已满足，就不用再比下去
                if (thisLineMet) {
                    break;
                }
            }

            // 如果这一行先修满足，则表示该课程的先修条件(OR逻辑)成功
            if (thisLineMet) {
                hasAnyPrereqSatisfied = true;
                break; 
            }
        }

        // 最后判断是否满足“至少一条先修项”
        return hasAnyPrereqSatisfied;
    }

    //=================== 生成全部组合 ===================
    private static List<List<Course>> generateAllPossiblePlans(Map<Integer,List<Course>> periodMap, int grade) {
        if (periodMap == null || periodMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> periods = new ArrayList<>(periodMap.keySet());
        Collections.sort(periods);

        List<List<Course>> result = new ArrayList<>();
        backtrack(periods, 0, periodMap, new ArrayList<>(), result, grade);
        return result;
    }

    private static void backtrack(List<Integer> periods,
                                  int idx,
                                  Map<Integer,List<Course>> periodMap,
                                  List<Course> current,
                                  List<List<Course>> result,
                                  int grade) {
        if (idx == periods.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        int p = periods.get(idx);
        List<Course> candidates = periodMap.getOrDefault(p, Collections.emptyList());
        if (candidates.isEmpty()) {
            return;
        }

        for (Course c : candidates) {
            current.add(c);
            if (partialCheckPlan(current, grade, idx, periods, periodMap)) {
                backtrack(periods, idx+1, periodMap, current, result, grade);
            }
            current.remove(current.size()-1);
        }
    }

    private static boolean partialCheckPlan(List<Course> plan,
                                            int grade,
                                            int idx,
                                            List<Integer> periods,
                                            Map<Integer,List<Course>> periodMap) {
        if (!checkNoDuplicateCourse(plan)) {
            return false;
        }
        if (!checkCourseLevelVariants(plan)) {
            return false;
        }
        if (grade == 10) {
            boolean hasEnglish10 = plan.stream()
                .anyMatch(c -> c.category == CourseCategory.ENGLISH && c.name.contains("English 10"));
            if (!hasEnglish10) {
                boolean possibleInFuture = false;
                for (int i = idx+1; i < periods.size(); i++) {
                    int p = periods.get(i);
                    List<Course> futureList = periodMap.get(p);
                    if (futureList != null) {
                        boolean found = futureList.stream()
                            .anyMatch(cc -> cc.category == CourseCategory.ENGLISH && cc.name.contains("English 10"));
                        if (found) {
                            possibleInFuture = true;
                            break;
                        }
                    }
                }
                if (!possibleInFuture) return false;
            }
        }
        return true;
    }

    private static boolean isFeasible(List<Course> plan, int grade) {
        if (!checkNoDuplicateCourse(plan)) return false;
        if (!checkCourseLevelVariants(plan)) return false;

        // === 强制：exactly 1 数学 + exactly 1 英语 ===
        List<Course> mathList = new ArrayList<>();
        List<Course> engList  = new ArrayList<>();
        for (Course c : plan) {
            if (c.category == CourseCategory.MATH) {
                mathList.add(c);
            } else if (c.category == CourseCategory.ENGLISH) {
                engList.add(c);
            }
        }
        if (mathList.size() != 1) return false;
        if (engList.size() != 1) return false;

        // 按年级检查
        boolean hasEnglishForGrade = false;
        for (Course c : plan) {
            if (c.category == CourseCategory.ENGLISH) {
                if (grade == 9 && c.name.contains("English 9")) hasEnglishForGrade = true;
                if (grade == 10 && c.name.contains("English 10")) hasEnglishForGrade = true;
                if (grade == 11 && (c.name.contains("English 11") || c.name.contains("AP English"))) hasEnglishForGrade = true;
                if (grade == 12 && (c.name.contains("English 12") || c.name.contains("AP English"))) hasEnglishForGrade = true;
            }
        }
        if (!hasEnglishForGrade) return false;

        return true;
    }

    private static boolean checkNoDuplicateCourse(List<Course> plan) {
        Set<String> bases = new HashSet<>();
        for (Course c : plan) {
            if (c.category == CourseCategory.FREE_PERIOD) continue;
            String base = getBaseCourseName(c.name);
            if (!bases.add(base)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkCourseLevelVariants(List<Course> plan) {
        Map<CourseCategory,Integer> catMaxLvl = new HashMap<>();
        for (Course c : plan) {
            if (c.category == CourseCategory.FREE_PERIOD) continue;
            int lvl = getLevel(c.name);
            catMaxLvl.put(c.category, Math.max(catMaxLvl.getOrDefault(c.category, 0), lvl));
        }
        for (Course c : plan) {
            if (c.category == CourseCategory.FREE_PERIOD) continue;
            int lvl = getLevel(c.name);
            int maxLvl = catMaxLvl.getOrDefault(c.category, 0);
            if (lvl < maxLvl) {
                return false;
            }
        }
        return true;
    }

    private static String getBaseCourseName(String name) {
        String s = name.replaceAll("\\(Fall\\)|\\(Spring\\)|/Fall|/Spring","")
                       .replaceAll("AP ","")
                       .replaceAll(", CP","")
                       .replaceAll(", Honors","")
                       .trim();
        return s;
    }

    private static void printAPRecommendation(MajorDirection direction) {
        System.out.println("根据您的专业方向，推荐的AP课程有：");
        switch(direction) {
            case STEM:
                System.out.println("AP Calculus AB/BC, AP Physics I, AP Chemistry, AP Computer Science A/1Principles, AP Statistics");
                break;
            case MEDICAL:
                System.out.println("AP Biology, AP Chemistry, AP Psychology, AP Calculus AB/BC, AP Statistics");
                break;
            case BUSINESS:
                System.out.println("AP Microeconomics, AP Macroeconomics, AP Calculus AB, AP Statistics, AP English Language and Composition");
                break;
            case SOCIAL_SCIENCE:
                System.out.println("AP US History, AP World History(如有), AP Psychology, AP Government and Politics, AP English Literature and Composition");
                break;
            case ENVIRONMENTAL:
                System.out.println("AP Environmental Science, AP Biology, AP Chemistry, AP Statistics, AP Human Geography(如有)");
                break;
            case CS_DATA:
                System.out.println("AP Computer Science A, AP Computer Science Principles, AP Calculus AB/BC, AP Statistics, AP Physics(如有)");
                break;
            case LANGUAGE_CULTURE:
                System.out.println("AP English Language/Literature, AP World Language(如有), AP Human Geography(如有), AP Comparative Government and Politics, AP World History");
                break;
            case LAW_POLICY:
                System.out.println("AP US Government and Politics, AP Comparative Government and Politics, AP US History, AP English Language and Composition, AP Psychology");
                break;
        }
    }

    private static double averageGPA(List<Course> plan) {
        double sum = 0; 
        int count = 0;
        for (Course c: plan) {
            if (c.category != CourseCategory.FREE_PERIOD) {
                sum += c.gpa;
                count++;
            }
        }
        return (count == 0) ? 0 : (sum / count);
    }

    private static double averageDifficulty(List<Course> plan) {
        double sum = 0; 
        int count = 0;
        for (Course c: plan) {
            if (c.category != CourseCategory.FREE_PERIOD) {
                sum += c.difficulty;
                count++;
            }
        }
        return (count == 0) ? 0 : (sum / count);
    }

    private static void printHighestGPAPlans(List<List<Course>> feasiblePlans,
                                             MajorDirection direction) {
        double maxGPA = -1;
        for (List<Course> plan : feasiblePlans) {
            double g = averageGPA(plan);
            if (g > maxGPA) {
                maxGPA = g;
            }
        }
        List<List<Course>> topPlans = new ArrayList<>();
        for (List<Course> plan : feasiblePlans) {
            if (Math.abs(averageGPA(plan) - maxGPA) < 1e-9) {
                topPlans.add(plan);
            }
        }
        if (topPlans.isEmpty()) {
            System.out.println("无可行方案");
            return;
        }

        Map<String, List<List<Course>>> grouped = new LinkedHashMap<>();
        for (List<Course> plan : topPlans) {
            String mathName = "";
            String engName  = "";
            for (Course c : plan) {
                if (c.category == CourseCategory.MATH)    mathName = c.name;
                if (c.category == CourseCategory.ENGLISH) engName  = c.name;
            }
            String key = mathName + " & " + engName;
            grouped.computeIfAbsent(key, k->new ArrayList<>()).add(plan);
        }

        System.out.println("共找到 " + grouped.size() + " 个可行(数学,英语)组合的最高GPA方案：");
        int idx = 0;
        for (Map.Entry<String, List<List<Course>>> en : grouped.entrySet()) {
            idx++;
            System.out.println("\n--- 组合 " + idx + " (" + en.getKey() + ") ---");
            printMergedByPeriod(en.getValue(), direction);
        }
    }

    private static void printMostRelevantPlan(List<List<Course>> feasiblePlans,
                                              MajorDirection direction) {
        System.out.println("与专业最相关方案(逐period挑选):");
        Map<Integer,Set<Course>> map = new HashMap<>();
        for (List<Course> plan : feasiblePlans) {
            for (Course c : plan) {
                map.computeIfAbsent(c.period, k->new HashSet<>()).add(c);
            }
        }
        List<Integer> sortedPeriods = new ArrayList<>(map.keySet());
        Collections.sort(sortedPeriods);

        for (Integer p : sortedPeriods) {
            List<Course> cands = map.get(p).stream()
                    .filter(c->c.category != CourseCategory.FREE_PERIOD)
                    .collect(Collectors.toList());

            if (cands.isEmpty()) {
                System.out.println("Period " + p + ": <<<FREE>>>");
                continue;
            }
            cands = cands.stream()
                    .filter(c-> isCourseInMajor(direction,c))
                    .collect(Collectors.toList());
            if (cands.isEmpty()) {
                System.out.println("Period " + p + ": <<<FREE>>>");
                continue;
            }
            double maxRel = cands.stream().mapToDouble(cc->cc.relevance).max().orElse(-1);
            List<Course> same = cands.stream()
                                     .filter(cc->Math.abs(cc.relevance - maxRel)<1e-9)
                                     .collect(Collectors.toList());
            if (same.size() == 1) {
                System.out.println("Period " + p + ": " + same.get(0).name);
            } else {
                String names = same.stream()
                                   .map(x->x.name)
                                   .collect(Collectors.joining(", "));
                System.out.println("Period " + p + ": (" + names + ")  //并列最相关");
            }
        }
    }

    private static void printEasiestPlans(List<List<Course>> feasiblePlans,
                                          MajorDirection direction) {
        double minDiff = Double.MAX_VALUE;
        for (List<Course> plan : feasiblePlans) {
            double d = averageDifficulty(plan);
            if (d < minDiff) {
                minDiff = d;
            }
        }
        List<List<Course>> easiest = new ArrayList<>();
        for (List<Course> plan : feasiblePlans) {
            if (Math.abs(averageDifficulty(plan) - minDiff) < 1e-9) {
                easiest.add(plan);
            }
        }
        if (easiest.isEmpty()) {
            System.out.println("无可行方案");
            return;
        }

        Map<String, List<List<Course>>> grouped = new LinkedHashMap<>();
        for (List<Course> plan : easiest) {
            String mathName = "";
            String engName  = "";
            for (Course c : plan) {
                if (c.category == CourseCategory.MATH)    mathName = c.name;
                if (c.category == CourseCategory.ENGLISH) engName  = c.name;
            }
            String key = mathName + " & " + engName;
            grouped.computeIfAbsent(key, k->new ArrayList<>()).add(plan);
        }

        System.out.println("共找到 " + grouped.size() + " 个(数学,英语)组合的最低难度方案：");
        int idx = 0;
        for (Map.Entry<String,List<List<Course>>> en : grouped.entrySet()) {
            idx++;
            System.out.println("\n--- 组合 " + idx + " (" + en.getKey() + ") ---");
            printMergedByPeriod(en.getValue(), direction);
        }
    }

    private static void printMergedByPeriod(List<List<Course>> plans,
                                            MajorDirection direction) {
        Set<Integer> allPeriods = new HashSet<>();
        for (List<Course> plan : plans) {
            for (Course c : plan) {
                allPeriods.add(c.period);
            }
        }
        List<Integer> sorted = new ArrayList<>(allPeriods);
        Collections.sort(sorted);

        for (Integer p : sorted) {
            Set<String> set = new LinkedHashSet<>();
            for (List<Course> plan : plans) {
                for (Course c : plan) {
                    if (c.period == p) {
                        set.add(c.name);
                    }
                }
            }
            if (set.size() == 1) {
                System.out.println("Period " + p + ": " + set.iterator().next());
            } else {
                String multi = String.join(", ", set);
                System.out.println("Period " + p + ": (" + multi + ")");
            }
        }
    }

    private static boolean isCourseInMajor(MajorDirection direction, Course c) {
        switch(direction) {
            case STEM:
                return c.category == CourseCategory.MATH
                    || c.category == CourseCategory.SCIENCE
                    || (c.category == CourseCategory.LIFE_CAREERS &&
                       (c.name.contains("Computer") || c.name.contains("Programming")));
            case MEDICAL:
                if (c.category == CourseCategory.SCIENCE) return true;
                if (c.name.contains("Physiology") || c.name.contains("Psychology")) return true;
                return false;
            case BUSINESS:
                if (c.category == CourseCategory.FINANCIAL) return true;
                if (c.name.contains("Business") || c.name.contains("Marketing") || c.name.contains("Economics")) return true;
                return false;
            case SOCIAL_SCIENCE:
                if (c.category == CourseCategory.SOCIAL_STUDIES) return true;
                if (c.name.contains("Sociology") || c.name.contains("Anthropology") || c.name.contains("Psychology")) {
                    return true;
                }
                return false;
            case ENVIRONMENTAL:
                if (c.name.contains("Environmental")) return true;
                if (c.category == CourseCategory.SCIENCE && c.name.contains("Chemistry")) return true;
                return false;
            case CS_DATA:
                if (c.category == CourseCategory.LIFE_CAREERS &&
                   (c.name.contains("Computer") || c.name.contains("Web") || c.name.contains("Programming"))) {
                    return true;
                }
                if (c.category == CourseCategory.MATH &&
                   (c.name.contains("Calculus") || c.name.contains("Statistics"))) {
                    return true;
                }
                return false;
            case LANGUAGE_CULTURE:
                if (c.category == CourseCategory.WL) return true;
                if (c.category == CourseCategory.ENGLISH && c.name.contains("English")) return true;
                if (c.category == CourseCategory.SOCIAL_STUDIES &&
                   (c.name.contains("History") || c.name.contains("Culture"))) {
                    return true;
                }
                return false;
            case LAW_POLICY:
                if (c.category == CourseCategory.SOCIAL_STUDIES &&
                   (c.name.contains("Government") || c.name.contains("Politics") || c.name.contains("History"))) {
                    return true;
                }
                if (c.name.contains("Economics") || c.name.contains("Law")) return true;
                return false;
            default:
                return false;
        }
    }
}
