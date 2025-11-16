package com.cangsg.brick.kt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.cangsg.brick.kt.entry.KtTest;
import com.cangsg.brick.kt.entry.KtTest10;
import com.cangsg.brick.kt.entry.KtTest2;
import com.cangsg.brick.kt.entry.KtTest2Item;
import com.cangsg.brick.kt.entry.KtTest3;
import com.cangsg.brick.kt.entry.KtTest4;
import com.cangsg.brick.kt.entry.KtTest5;
import com.cangsg.brick.kt.entry.KtTest6;
import com.cangsg.brick.kt.entry.KtTest7;
import com.cangsg.brick.kt.entry.KtTest8;
import com.cangsg.brick.kt.entry.KtTest9;
import com.cangsg.brick.kt.entry.KtTestItem;
import com.cangsg.brick.kt.entry.KtValidateResult;

class KtValidateUtilsTests {

	@Test
	void test1() {
		KtTest user = new KtTest();

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("用户名不能为空", result.getErrorMsg());
	}

	@Test
	void test2() {
		KtTest user = new KtTest();
		user.setLogin("abc");

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("字符串必须5到10个字符", result.getErrorMsg());
	}

	@Test
	void test3() {
		KtTest user = new KtTest();
		user.setLogin("abcde");

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("密码不能为空", result.getErrorMsg());
	}

	@Test
	void test4() {
		KtTest user = new KtTest();
		user.setLogin("abcde");
		user.setPassword("123");
		user.setMap(null);

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("Map不能为空", result.getErrorMsg());
	}

	@Test
	void test5() {
		KtTest user = new KtTest();
		user.setLogin("abcde");
		user.setPassword("123a");

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("密码格式异常", result.getErrorMsg());
	}

	@Test
	void test6() {
		KtTest user = new KtTest();
		user.setLogin("abcde");
		user.setPassword("123");

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("Map数量不能为空", result.getErrorMsg());

		user.setMap(null);
		KtValidateResult<String> result2 = KtValidateUtils.valid(user);
		assertNotNull(result2);
		assertEquals("Map不能为空", result2.getErrorMsg());
	}

	@Test
	void test7() {
		KtTest user = new KtTest();
		user.setLogin("abcde");
		user.setPassword("123");
		user.getMap().put("k1", KtTestItem.builder().build());

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("用户名不能为空", result.getErrorMsg());
	}

	@Test
	void test8() {
		KtTest user = new KtTest();
		user.setLogin("abcde");
		user.setPassword("123");
		user.getMap().put("k1", KtTestItem.builder().name("abc").build());

		KtValidateResult<String> result = KtValidateUtils.valid(user);
		assertNotNull(result);
		assertEquals("用户名大于4个字符，小于50个字符", result.getErrorMsg());
	}

	@Test
	void test9() {
		KtTest2 test2 = new KtTest2();
		test2.setItems(new ArrayList<>());
		test2.setItems2(new ArrayList<>());

		KtValidateResult<String> result = KtValidateUtils.valid(test2);
		assertNotNull(result);
		assertEquals("列表数量不能为空", result.getErrorMsg());
		test2.getItems().add("123");

		test2.getItems2().add(KtTest2Item.builder().name("1234").build());
		KtValidateResult<String> result2 = KtValidateUtils.valid(test2);
		assertEquals("名称必须3个数字", result2.getErrorMsg());
	}

	@Test
	void test10() {
		KtTest3 test3 = new KtTest3();
		test3.setItems(new String[] { "123", "456" });
		test3.setItems2(new KtTest2Item[] { KtTest2Item.builder().name("1234").build() });

		KtValidateResult<String> result = KtValidateUtils.valid(test3);
		assertNotNull(result);
		assertEquals("名称必须3个数字", result.getErrorMsg());
	}

	@Test
	void test11() {
		KtTest4 test4 = new KtTest4();
		test4.setType("k1");

		KtValidateResult<String> result = KtValidateUtils.valid(test4);
		assertNotNull(result);
		assertEquals("只能选择给定类型", result.getErrorMsg());
	}

	@Test
	void test12() {
		KtTest5 test5 = new KtTest5();
		test5.setStartDate(new Date(2025, 9, 1));
		test5.setEndDate(new Date(2025, 8, 3));

		KtValidateResult<String> result = KtValidateUtils.valid(test5);
		assertNotNull(result);
		assertEquals("开始时间必须小于结束时间", result.getErrorMsg());

	}

	@Test
	void test13() {
		KtTest6 test6 = new KtTest6();
		test6.setStartDate(LocalDate.of(2025, 9, 1));
		test6.setEndDate(LocalDate.of(2025, 8, 3));

		KtValidateResult<String> result = KtValidateUtils.valid(test6);
		assertNotNull(result);
		assertEquals("开始时间必须小于结束时间", result.getErrorMsg());
	}

	@Test
	void test14() {
		KtTest7 test7 = new KtTest7();
		test7.setStartDate(LocalDateTime.of(2025, 9, 1, 12, 1, 1));
		test7.setEndDate(LocalDateTime.of(2025, 8, 3, 12, 1, 1));

		KtValidateResult<String> result = KtValidateUtils.valid(test7);
		assertNotNull(result);
		assertEquals("开始时间必须小于结束时间", result.getErrorMsg());
	}

	@Test
	void test15() {
		KtTest8 test8 = new KtTest8();
		test8.setI1(1);
		test8.setI2(12);

		KtValidateResult<String> result = KtValidateUtils.valid(test8);
		assertNotNull(result);
		assertEquals("数字范围3-7", result.getErrorMsg());

		test8.setI1(3);
		test8.setI2(1);

		KtValidateResult<String> result2 = KtValidateUtils.valid(test8);
		assertNotNull(result2);
		assertEquals("数字范围10-12", result2.getErrorMsg());
	}

	@Test
	void test16() {
		KtTest9 test9 = new KtTest9();
		test9.setF1(0.5f);
		test9.setF2(7.2f);

		KtValidateResult<String> result = KtValidateUtils.valid(test9);
		assertNotNull(result);
		assertEquals("数字范围1.5-5.5", result.getErrorMsg());

		test9.setF1(5.2f);
		test9.setF2(9.0f);

		KtValidateResult<String> result2 = KtValidateUtils.valid(test9);
		assertNotNull(result2);
		assertEquals("数字范围6.0-8.5", result2.getErrorMsg());
	}

	@Test
	void test17() {
		KtTest10 test10 = new KtTest10();
		test10.setI1(9);
		test10.setS1("hi");

		KtValidateResult<String> result2 = KtValidateUtils.valid(test10);
		assertNotNull(result2);
		assertEquals("hi9", test10.getS1());
		assertEquals("s1校验规则不正确", result2.getErrorMsg());
	}

}
