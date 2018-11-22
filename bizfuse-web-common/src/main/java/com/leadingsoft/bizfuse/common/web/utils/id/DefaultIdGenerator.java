package com.leadingsoft.bizfuse.common.web.utils.id;

/**
 * 缺省流水号ID生成器。 <br>
 * long类型数值的范围： 999+233720+3685+4774807 (64bit) <br>
 * 生成规则： SiteID(3位) + YYMMDD(6位) + hhmm(4位) + 流水号(7位)。 <br>
 * 7位流水号可以满足每天1000万个ID分配， <br>
 * 算法上允许流水号溢出，溢出后在原来时间上加一分钟后重置流水号从零分配。 <br>
 * 每天第一次生成ID时会自动重置流水号。<br>
 * 若需要每天零时重置流水号，需要外部在零时调用reset方法。<br>
 * 每次重新启动JVM会重置流水号，一分钟间隔以上重启理论上不会产生重复流水号。<br>
 * 注意： 一分钟之内重启两次会产生重复ID；一分钟内生成1000万以上ID后，下一分钟重启也会产生重复ID，需要规避。<br>
 * 使用 SiteID作为集群JVM区别码（缺省一个JVM时为20），支持00~91共92个JVM区别码。<br>
 * 若需要更多集群节点（JVM区别码）的支持，可以使用一下算法生成ID（支持8192个）。<br>
 * SiteID(13bit) + YYMMDD(6+4+5bit) + hhmm(5+6bit) + 流水号(24bit)。<br>
 */
public class DefaultIdGenerator extends BaseIdGenerator {

	/**
	 * twitter的snowflake算法
	 */
	private SnowFlake snowFlake;
	
	public DefaultIdGenerator(long siteId, String prefix) {
		super(siteId, prefix);
		snowFlake = new SnowFlake(this.datacenterId, this.siteId);
	}
	
	public DefaultIdGenerator(long datacenterId, long siteId, String prefix) {
		super(datacenterId, siteId, prefix);
		snowFlake = new SnowFlake(this.datacenterId, this.siteId);
	}

	@Override
	public void reset() {
		snowFlake = new SnowFlake(this.datacenterId, this.siteId);
	}

	@Override
	public long generate() {
		return this.snowFlake.nextId();
	}
	
	public static void main(String[] args) {
		DefaultIdGenerator generator = new DefaultIdGenerator(12, "C");
		for(int i = 0; i < 100; i++) {
			System.out.println(generator.generateCode());
		}
	}
}
