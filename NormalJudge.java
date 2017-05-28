import java.io.File;
import java.io.FileReader;

/**
 * Created by liz on 17/5/25.
 */
public class NormalJudge {
    private static int[][] testArray;
    private static int[] newArray;

    /**
     *
     * @param array 样本数组
     * @param t     新样本数
     * @return
     */
    private static int judge(int[] array,int t) {
        double mean = 0;            //  样本均值
        double variance = 0;        //  样本方差
        for(int i = 0; i < array.length; i++) {
            mean += array[i];
            System.out.print(array[i] + "\t");
        }
        System.out.println("\n" + t);
        mean /= array.length;

        for(int i = 0; i < array.length; i++) {
            variance += array[i]*array[i];
        }
        variance -= array.length * mean * mean;
        variance /= (array.length - 1);

        double standardDeviation = Math.sqrt(variance);

        if (t > mean - standardDeviation && t < mean + standardDeviation)
            return 10;
        else if (t > mean - 2 * standardDeviation && t < mean + 2 * standardDeviation)
            return 5;
        else if (t > mean - 3 * standardDeviation && t < mean + 3 * standardDeviation)
            return 1;
        else
            return 0;

    }

    public static void main(String[] args) {
        try {
            File OriginFile = new File("keystroke-dynamics.txt");
            File TestFile = new File("KD_exam.txt");
            FileReader fileReader1 = new FileReader(OriginFile);
            char[] chars = new char[3000];
            fileReader1.read(chars);
            String[] strings = String.valueOf(chars).split("\\s+");
//            for (String string: strings) {
//                System.out.println(string);
//            }
            int count = 0;
            testArray = new int[40][10];
            for (int i = 0; i < 40; i++) {
                for (int j = 0; j < 10; j++) {
                    testArray[i][j] = Integer.parseInt(strings[count++]);
//                    System.out.println(testArray[i][j]);
                }
            }
            FileReader fileReader2 = new FileReader(TestFile);
            char[] chars2 = new char[100];
            fileReader2.read(chars2);
            String[] strings2 = String.valueOf(chars2).split("\\s+");
            newArray = new int[10];
            for(int i = 0; i < 10; i++) {
                newArray[i] = Integer.parseInt(strings2[i]);
            }

            int[] tmpArray = new int[40];
            count = 0;
            for(int i = 0; i < 10; i++) {
                for (int j = 0; j < 40; j++) {
                    tmpArray[j] = testArray[j][i];
                }
                count += judge(tmpArray,newArray[i]);
            }

            System.out.println(count);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
