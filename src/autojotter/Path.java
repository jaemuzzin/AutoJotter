
package autojotter;

import java.util.ArrayList;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author Jae
 */
public class Path {
    private ArrayList<SimpleMatrix> points;
private String upcommand;
private String downcommand;

    public Path(List<SimpleMatrix> points, String upcommand, String downcommand) {
        this.points = new ArrayList<>(points);
        this.upcommand = upcommand;
        this.downcommand = downcommand;
    }

    public ArrayList<SimpleMatrix> getPoints() {
        return points;
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("G0 X%7f Y%7f\r\n", points.get(0).get(0, 0), points.get(0).get(1,0)));
        sb.append(downcommand+"\r\n");
        for(SimpleMatrix m : points){
            
            sb.append(String.format("G1 X%7f Y%7f\r\n", m.get(0, 0), m.get(1,0)));
        }
        sb.append(upcommand+"\r\n");
        return sb.toString();
    }
}
