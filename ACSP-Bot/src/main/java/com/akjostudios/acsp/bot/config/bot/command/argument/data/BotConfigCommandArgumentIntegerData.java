package com.akjostudios.acsp.bot.config.bot.command.argument.data;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unused")
public class BotConfigCommandArgumentIntegerData extends BotConfigCommandArgumentData<Integer> {
	private Integer minValue;
	private Integer maxValue;
	private Integer defaultValue;

	public BotConfigCommandArgumentIntegerData(
			Integer minValue,
			Integer maxValue,
			Integer defaultValue
	) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.defaultValue = defaultValue;
	}
}