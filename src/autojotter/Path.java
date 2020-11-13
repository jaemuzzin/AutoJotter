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
	private boolean svg;

	public Path(List<SimpleMatrix> points, String upcommand, String downcommand, boolean svg) {
		this.points = new ArrayList<>(points);
		this.upcommand = upcommand;
		this.downcommand = downcommand;
		this.svg = svg;
	}

	public ArrayList<SimpleMatrix> getPoints() {
		return points;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!svg) {
			sb.append(String.format("G0 X%7f Y%7f\r\n", points.get(0).get(0, 0), points.get(0).get(1, 0)));
			sb.append(downcommand + "\r\n");
			for (SimpleMatrix m : points) {

				sb.append(String.format("G1 X%7f Y%7f\r\n", m.get(0, 0), m.get(1, 0)));
			}
			sb.append(upcommand + "\r\n");
		} else {
			sb.append("<path style=\"opacity:1;vector-effect:none;fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.26458332px;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1\" d=\"");
			boolean skipped=false;
                        for (SimpleMatrix m : points) {
                            if(!skipped){
                                sb.append(String.format("M %7f,%7f ", m.get(0, 0), m.get(1, 0)));
                                skipped=true;
                                continue;
                            }
                            sb.append(String.format("L %7f,%7f ", m.get(0, 0), m.get(1, 0)));
			}
                        
			//sb.append("id=\"path" + this.hashCode() + "\"\n");
			sb.append("\" inkscape:connector-curvature=\"0\" />");
		}
		return sb.toString();
	}
}
