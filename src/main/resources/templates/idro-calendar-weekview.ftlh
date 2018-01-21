<span class="page-title aligncenter">Kalender ${year}</span>
[accordion]
<#list months as month>
[toggle title="${month.name}" state="${month.state}"]
<div class="d-calendar">
	<div class="d-row d-header"></div>
	<div class="d-cell d-header d-columnfirst">
		weken
	</div>
	<#list month.weeks as week>
	<div class="d-cell d-header d-large">${week.start} - ${week.end}</div>
	<div class="d-cell d-header d-small">${week.weekNumber}</div>
	</#list>
	<div class="d-row"></div>
	<#list month.casas as casa>
		<div class="d-cell d-columnfirst">
			<a href="${casa.link}">${casa.name}</a>
		</div>
		<#list casa.periodBookings as periodBooking>
			<div class="d-cell ${periodBooking.state}"></div>
		</#list>
		<div class="d-row"></div>
	</#list>
</div>
[/toggle]
</#list>
[/accordion]
