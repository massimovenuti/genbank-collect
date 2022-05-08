package Interface;

import org.NcbiParser.OverviewData;

import java.util.ArrayList;

public class Processing {
    public static ArrayList<OverviewData> getChecked(JCheckBoxTree tree) {
        var ret = new ArrayList<OverviewData>();
        var cPaths = tree.getCheckedPaths();
        ret.ensureCapacity(cPaths.length);
        for (var p : cPaths) {
            /* Note: 0 = root, 1= Kingdom, 2= Group, 3= SGroup, 4=Organism */
            var pcount = p.getPathCount();
            var kingdom = pcount > 1 ? p.getPathComponent(1).toString() : null;
            var group = pcount > 2 ? p.getPathComponent(2).toString() : null;
            var sgroup = pcount > 3 ? p.getPathComponent(3).toString() : null;
            var og = pcount > 4 ? p.getPathComponent(4).toString() : null;
            var od = new OverviewData(kingdom, group, sgroup, og);
            //System.out.printf("%2d -> %40s\n", p.getPathCount(), p.toString());
            //System.out.printf("%10s - %10s - %10s - %10s\n", od.getKingdom(), od.getGroup(), od.getSubgroup(), od.getOrganism());
            ret.add(od);
        }
        return ret;
    }
}
