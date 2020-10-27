package autojotter;

import com.piro.bezier.BezierPath;
import com.piro.bezier.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author Jae
 */
public class Glyph {

    private List<Path> paths = new ArrayList<Path>();
    char rep = ' ';

    public Glyph() {
    }

    public Glyph(String svgFile, char rep, String up, String down) {
        this.rep = rep;
        Pattern regex = Pattern.compile("[^\\w]d=\"([\\w\\. ,-]*)\"");
        Matcher mathcer = regex.matcher(svgFile);
        while (mathcer.find()) {
            BezierPath path = new BezierPath();
            path.parsePathString(mathcer.group(1));
            ArrayList<SimpleMatrix> next = new ArrayList<>();
            for (float i = 0; i <= 1; i += .015) {
                Vector2 p = path.eval(i);
                //inkscape adds 271
                p.y-=271;
                //change origin to bottom left
                p.y = -1*p.y + 25.4f;
                SimpleMatrix sm = new SimpleMatrix(new double[][]{new double[]{p.x}, new double[]{p.y}, new double[]{1}});
                next.add(sm);
            }
            paths.add(new Path(next, up, down));
        }
    }

    public Glyph(List<Path> opaths, char rep, String upcommand, String downcommand) {
        this.rep = rep;
        paths = opaths.stream().map(p
                -> new Path(p.getPoints().stream().collect(Collectors.toList()), upcommand, downcommand))
                .collect(Collectors.toList());
    }


    public List<Path> getPaths() {
        return paths;
    }

    @Override
    public String toString() {
        return "" + rep;
    }

}
