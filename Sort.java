package zk.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Sort
{
	private static Random random = new Random(System.currentTimeMillis());
	
	public static <T> void quickSort(T[] array, Comparator<T> comp)
	{
		qSort(array, 0, array.length - 1, comp);
	}

	private static <T> void qSort(T[] array, int a, int b, Comparator<T> comp)
	{
		if(a < b)
		{
			int i = partition(array, a, b, comp);
			qSort(array, a, i - 1, comp);
			qSort(array, i + 1, b, comp);
		}
	}
	private static <T> int partition(T[] array, int a, int b, Comparator<T> comp)
	{
		int r = random.nextInt(b - a + 1) + a;
		T key = array[r];
		swap(array, r, b);
		int index = a;
		for(int i = index; i < b; i++)
			if(comp.compare(array[i], key) < 0)
				swap(array, i, index++);
		swap(array, index, b);
		return index;
	}
	
	public static void mergSort(int[] array)
	{
		mergeSort(array,  array.length - 1, 0);
	}
	
	private static void mergeSort(int[] array, int high, int low)
	{
		if(low < high)		
		{
			int mid = (high + low) >>> 1;
			mergeSort(array, high, mid + 1);
			mergeSort(array, mid, low);
			merge(array, high, mid, low);
		}
	}
	
	private static void merge(int[] array, int high, int mid, int low)
	{
		int i = low;
		int j = mid + 1;
		int k = 0;
		int len = high - low + 1;
		int[] temp = new int[len];
		while(i <= mid && j <= high)
		{
			if(array[i] < array[j])
				temp[k++] = array[i++];
			else
				temp[k++] = array[j++];
		}
		
		while(i <= mid)
			temp[k++] = array[i++];
		while(j <= high)
			temp[k++] = array[j++];
		
		System.arraycopy(temp, 0, array, low, len);
	}

	public static void swap(Object[] array, int a, int b)
	{
		Object o = array[a];
		array[a] = array[b];
		array[b] = o;
	}
	
	public static void bubbleSort(int[] array)
	{
		for(int i = 0; i < array.length; i++)
			for(int j = i; j > 0 && array[j - 1] > array[j]; j--)
				swap(array, j, j - 1);
	}

	private static void swap(int[] array, int a, int b)
	{
		int i = array[a];
		array[a] = array[b];
		array[b] = i;
	}
	
	public static void countingSort(int[] array, int max)
	{
		int[] k = new int[max];
		int[] tmp = Arrays.copyOf(array, array.length);
		for(int i = 0; i < array.length; i++)
			k[tmp[i]]++;
		
		for(int i = 1; i < k.length; i++)
			k[i] += k[i - 1];
		for(int i = array.length - 1; i >= 0; i--)
		{
			array[k[tmp[i]] - 1] = tmp[i];
			k[tmp[i]]--;
		}
	}
	
	public static void radixSort(int[] array)
	{
		for(int i = 0; i < 32; i+= 8)
		{
			int[] k = new int[256];
			int[] tmp = Arrays.copyOf(array, array.length);
			for(int j = 0; j < array.length; j++)
				k[shift(tmp[j], i)]++;
			for(int j = 1; j < k.length; j++)
				k[j] += k[j - 1];
			for(int j = array.length - 1; j >= 0; j--)
			{
				int a = shift(tmp[j], i);
				array[k[a] - 1] = tmp[j];
				k[a]--;
			}
		}
	}
	
	private static int shift(int i, int s)
	{
		return (i >> s) & 0xff;
	}
	
	public static int kthElement(int[] array, int k)
	{
		return randomSelect(array, 0, array.length - 1, k);
	}
	
	private static int randomSelect(int[] array, int p, int q, int i)
	{
		if(p == q)
			return array[p];
		int r = partition(array, p, q);
		int k = r - p + 1;
		if(k == i)
			return array[r];
		else if(k > i)
			return randomSelect(array, p, r - 1, i);
		else
			return randomSelect(array, r + 1, q, i - k);
	}
	
	private static int partition(int[] array, int p, int q)
	{
		int r = random.nextInt(q - p + 1) + p;
		int key = array[r];
		swap(array, r, q);
		int index = p;
		for(int i = index; i < q; i++)
			if(array[i] < key)
				swap(array, i, index++);
		swap(array, index, q);
		return index;
	}
	
	public static void insertionSort(int[] array)
	{
		for(int i = 1; i < array.length; i++)
		{
			int j = 0;
			for(; j < i && array[i] > array[j]; j++);
			for(int k = j; k < i; k++)
				swap(array, k, i);
		}
	}
	
	public static void selectSort(int[] array)
	{
		for(int i = 0; i < array.length; i++)
		{
			int min = i;
			for(int j = i; j < array.length; j++)
				if(array[j] < array[min])
					min = j;
			swap(array, i, min);
		}
	}
	
	public static int findMedian(int[] array)
	{
		int l = array.length;
		if(l % 2 == 0)
		{
			int m1 = l/2;
			int m2 = m1 + 1;
			int x = randomSelect(array, 0, array.length - 1, m1);
			int y = randomSelect(array, 0, array.length - 1, m2);
			return (x + y) / 2;
		}
		else
		{
			int m = l / 2 + 1;
			return randomSelect(array, 0, array.length - 1, m);
		}
	}
	
	
}
