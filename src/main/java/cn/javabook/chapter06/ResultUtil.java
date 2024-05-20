package cn.javabook.chapter06;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 结果返回工具类
 *
 */
public abstract class ResultUtil {
	private static JSONObject results = null;

	/*
	 * 成功时返回
	 */
	public static JSONObject success() {
		results = new JSONObject();
		results.put("errcode", Constant.SUCCESS_CODE);
		results.put("errmsg", "");
		JSONObject result = new JSONObject();
		result.put("items", new JSONArray(Constant.DEFAULT_ONE_SIZE));
		result.put("content", new JSONObject());
		result.put("extra", "");
		results.put("result", result);
		return results;
	}

	/*
	 * 成功时返回，带有错误码
	 */
	public static JSONObject success(final int errcode) {
		results = new JSONObject();
        results.put("errcode", errcode);
		results.put("errmsg", "");
		JSONObject result = new JSONObject();
		result.put("items", new JSONArray(Constant.DEFAULT_EMPTY_SIZE));
		result.put("content", new JSONObject());
		result.put("extra", "");
		results.put("result", result);
		return results;
	}

	/*
	 * 失败时返回
	 */
	public static JSONObject failure(final int errcode, final String errmsg) {
		results = new JSONObject();
		results.put("errcode", errcode);
		results.put("errmsg", errmsg);
		JSONObject result = new JSONObject();
		result.put("items", new JSONArray(Constant.DEFAULT_EMPTY_SIZE));
		result.put("content", new JSONObject());
		result.put("extra", "");
		results.put("result", result);
		return results;
	}

	/*
	 * 返回数组
	 */
	public static JSONObject itmes(final JSONArray items) {
		results = new JSONObject();
		results.put("errcode", Constant.SUCCESS_CODE);
		results.put("errmsg", "");
		JSONObject result = new JSONObject();
		result.put("items", items);
		result.put("content", new JSONObject());
		result.put("extra", "");
		results.put("result", result);
		return results;
	}

	/*
	 * 返回对象
	 */
	public static JSONObject content(final JSONObject content) {
		results = new JSONObject();
		results.put("errcode", Constant.SUCCESS_CODE);
		results.put("errmsg", "");
		JSONObject result = new JSONObject();
		result.put("items", new JSONArray(Constant.DEFAULT_EMPTY_SIZE));
		result.put("content", content);
		result.put("extra", "");
		results.put("result", result);
		return results;
	}

	/*
	 * 返回附加信息
	 */
	public static JSONObject extra(final String extra) {
		results = new JSONObject();
		results.put("errcode", Constant.SUCCESS_CODE);
		results.put("errmsg", "");
		JSONObject result = new JSONObject();
		result.put("items", new JSONArray(Constant.DEFAULT_EMPTY_SIZE));
		result.put("content", new JSONObject());
		result.put("extra", extra);
		results.put("result", result);
		return results;
	}

	/*
	 * 返回完整的结果
	 */
	public static JSONObject jsonResult(final JSONArray items, final JSONObject content, final String extra) {
		results = new JSONObject();
		results.put("errcode", Constant.SUCCESS_CODE);
		results.put("errmsg", "");
		JSONObject result = new JSONObject();
		result.put("items", items);
		result.put("content", content);
		result.put("extra", extra);
		results.put("result", result);
		return results;
	}

	/*
	 * 返回完整的结果
	 */
	public static JSONObject jsonResult(final int errcode, final String errmsg, final JSONArray items, final JSONObject content, final String extra) {
		results = new JSONObject();
		results.put("errcode", errcode);
		results.put("errmsg", errmsg);
		JSONObject result = new JSONObject();
		result.put("items", items);
		result.put("content", content);
		result.put("extra", extra);
		results.put("result", result);
		return results;
	}

	/*
	 * 回调成功时返回
	 */
	public static JSONObject callbackSuccess(final JSONObject data) {
		results = new JSONObject();
		results.put("code", Constant.HTTP_STATUS_OK);
		results.put("message", "request success");
		results.put("data", data);
		return results;
	}

	/*
	 * 回调失败时返回
	 */
	public static JSONObject callbackFailure(final int errcode, final String errmsg, final JSONObject data) {
		results = new JSONObject();
		results.put("code", errcode);
		results.put("message", errmsg);
		results.put("data", data);
		return results;
	}
}
