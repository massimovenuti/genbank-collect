package org.NcbiParser;

import java.util.ArrayList;

public class BinarySearchClass {
    public static int binarySearch(ArrayList<OverviewData> overviewData, int first, int last,String organismKey){
        if (last>=first){
            int mid = first + (last - first)/2;
            if (overviewData.get(mid).getOrganism().equalsIgnoreCase(organismKey)){
                return mid;
            }
            if (overviewData.get(mid).getOrganism().compareToIgnoreCase(organismKey) < 0){
                return binarySearch(overviewData, mid+1, last, organismKey);//search in left subarray
            }else{
                return binarySearch(overviewData, first, mid-1, organismKey);//search in right subarray
            }
        }
        return -1;
    }
}
