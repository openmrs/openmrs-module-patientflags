<div class="info-section">
    <div class="info-header">
        <h3>${ ui.message("patientflags.patientOverview.title") }</h3>
    </div>

    <div class="info-body">
    	<ul>
            <% flags.each { flag -> %>
                <li><span style="border: 1px solid #51a351; color: #51a351; padding: 1px 2px; border-radius: 4px;"> ${ flag } </span></li>
            <% } %>
    	</ul>
    </div>
</div>