package eu.rehost.tasks;

import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.configuration.paths.Maven2ProjectPaths;
import org.apache.torque.generator.control.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Taken from https://db.apache.org/torque/torque-5.1/documentation/orm-reference/running-the-generator.html
class generateOm() {
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<UnitDescriptor>();

        HashMap<String, String> overrideOptions = new HashMap<String, String>();
        overrideOptions.put("torque.om.package","${torque.target.package}");

        CustomProjectPaths projectPaths = new CustomProjectPaths(new Maven2DirectoryProjectPaths(new File(".")));

        projectPaths.setConfigurationPackage("org.apache.torque.templates.om");
        projectPaths.setConfigurationDir(null);
        projectPaths.setSourceDir(new

        File("${sourceDir}"));
        projectPaths.setOutputDirectory(
                null,
                new

        File("target/generated-sources"));
        projectPaths.setOutputDirectory(
        Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                new

        File("src/main/generated-java"));
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.CLASSPATH,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(
                new

        MapOptionsConfiguration(overrideOptions));
        unitDescriptors.add(unitDescriptor);

        controller.run(unitDescriptors);

}