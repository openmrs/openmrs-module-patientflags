class FlagsController {
    constructor(openmrsRest) {
        const vm = this;
        vm.flags = [];
        vm.flagData = {};

        openmrsRest.listFull('patientflags/flag').then((response) => {
            vm.flags = response.results;

            vm.flags.forEach((f) => {
                f.tags = f.tags.map((t) => t.name).join(' ');
                f.status = f.enabled ? 'Enabled' : 'Disabled';
            });
        });
    }
}

export default FlagsController;
