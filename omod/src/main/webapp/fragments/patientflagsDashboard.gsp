<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/index.htm" />

<div class="info-section">
    <div class="info-header">
        <h3>${ ui.message("patientflags.patientOverview.title") }</h3>
    </div>

    <div class="info-body">
    	<ul>
            <% flags.each { flag -> %>
                <li><span style=" ${ flag.priority.style } "> ${ flag.message } </span></li>
            <% } %>
    	</ul>
    </div>
</div>