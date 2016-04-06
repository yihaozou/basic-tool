package com.netease.api.exception;

public class CommonBusinessException extends Exception
{
	private static final long serialVersionUID = 6474693569280292459L;
	private Integer resultCode = null;
	private String msg = null;

	public CommonBusinessException(Integer resultCode, String msg)
	{
		super();
		this.resultCode = resultCode;
		this.msg = msg;
	}

	public CommonBusinessException(Integer resultCode)
	{
		super();
		this.resultCode = resultCode;
	}

	public CommonBusinessException()
	{
		super();
	}

	public Integer getResultCode()
	{
		return resultCode;
	}

	public void setResultCode(Integer resultCode)
	{
		this.resultCode = resultCode;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

}
