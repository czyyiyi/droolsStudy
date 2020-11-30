package code;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Solution {

    public int searchInsert(int[] nums, int target){
        int nums_len = nums.length;
        boolean t_isMIn = nums[0]>target;
        boolean t_isMax = nums[nums_len-1]<target;
        if(t_isMIn){
            return 0;
        }
        if(t_isMax){
            return nums_len;
        }

        int[] variance = new int[nums_len];
        for(int i = 0;i<nums_len;i++){
            variance[i]=nums[i]-target;
        }

        int pos = 0;
        int minV = nums[nums_len-1]+1;
        for(int i = 0;i<nums_len;i++){
            if(variance[i]>=0){
                if(variance[i]<=minV){
                    minV = variance[i];
                    pos = i;
                }
            }
        }
        return pos;
    }






    public int binarySearch(int[] nums, int target, int nums_len){
        int left = 0;
        int right = nums_len-1;
        while(left<=right){
            int mid = (left+right)/2;
            if(nums[mid]==target){
                return mid;
            }
            if(nums[mid]>target){
                right = mid-1;
            }
            if(nums[mid]<target){
                left = mid+1;
            }
        }
        return -1;
    }



    @Test
    public void test(){
        int[] nums1 = {1,3,5,6};
        int target1 = 5;

        int[] nums2 = {1,3,5,6};
        int target2 = 2;

        int[] nums3 = {1,3,5,6};
        int target3 = 7;

        int[] nums4 = {1,3,5,6};
        int target4 = 0;

        int[] nums5 = {1};
        int target5 = 0;
        
        Assert.assertEquals(searchInsert(nums1,target1),2);
        Assert.assertEquals(searchInsert(nums2,target2),1);
        Assert.assertEquals(searchInsert(nums3,target3),4);
        Assert.assertEquals(searchInsert(nums4,target4),0);
        Assert.assertEquals(searchInsert(nums5,target5),0);
    }
































































































}
