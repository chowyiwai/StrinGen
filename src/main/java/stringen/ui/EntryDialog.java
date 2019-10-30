package stringen.ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import stringen.Util;
import stringen.logic.Cohort;
import stringen.logic.Module;
import stringen.logic.Parser;
import stringen.logic.prerequisites.McPrerequisite;
import stringen.logic.prerequisites.ModulePrerequisite;
import stringen.logic.prerequisites.ModulePrerequisiteList;

public class EntryDialog extends GridPane {

    @FXML
    private ComboBox<String> cohortStart;
    @FXML
    private ComboBox<String> cohortEnd;
    @FXML
    private ComboBox<String> otherRequirement;
    @FXML
    private ComboBox<String> modulePrefix;
    @FXML
    private Button addCohortButton;
    @FXML
    private Button addOtherSubRequirementButton;
    @FXML
    private Button addSubRequirementButton;
    @FXML
    private Button addModPrereqButton;
    @FXML
    private VBox mcSubRequirements;
    @FXML
    private VBox otherRequirements;
    @FXML
    private AnchorPane entryDialog;
    @FXML
    private TextField mcs;
    @FXML
    private VBox modulePrereqs;

    public EntryDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CommandBox.class.getResource("/view/EntryDialog.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cohortStart.getItems().addAll(Util.YEARS);
        cohortStart.getSelectionModel().select("YEAR");
        cohortEnd.getItems().addAll(Util.YEARS);
        cohortEnd.getSelectionModel().select("YEAR");
    }

    public Module updateModule(Module module, Parser parser) {
        // get cohort year range
        String startYear = cohortStart.getSelectionModel().getSelectedItem();
        String endYear = cohortEnd.getSelectionModel().getSelectedItem();

        Cohort cohort = parser.parseCohort(startYear, endYear);
        updateCohort(cohort, parser);
        module.addNewCohort(cohort);
        return module;
    }

    public void updateCohort(Cohort cohort, Parser parser) {
        // get mc prerequisites
        McPrerequisite mcPrerequisite = null;
        if (!mcs.getText().equals("")) {
            mcPrerequisite = parser.parseMcPrerequisite(mcs.getText(), cohort);
        }

        // get module prerequisites
        ModulePrerequisiteList modPrereq = getModPrereqs(parser);
        cohort.setMcPrerequisite(mcPrerequisite);
        cohort.setModulePrerequisites(modPrereq);
    }

    @FXML
    private void createNewPane() {
        Parent parent = this.getParent();
        VBox entryDialogs = (VBox) parent;
        entryDialog.getChildren().remove(addCohortButton);
        entryDialogs.getChildren().add(new EntryDialog());
    }

    @FXML
    private void addSubRequirement() {
        mcSubRequirements.getChildren().remove(addSubRequirementButton);
        mcSubRequirements.getChildren().add(new McSubRequirement());
        mcSubRequirements.getChildren().add(addSubRequirementButton);
    }

    @FXML
    private void createNewOtherSubRequirement() {
        otherRequirements.getChildren().remove(addOtherSubRequirementButton);
        ObservableList<Node> children = otherRequirements.getChildren();
        for (Node child : children) {
            if (child instanceof OtherSubRequirement) {
                OtherSubRequirement childRequirements = (OtherSubRequirement) child;
                childRequirements.enableOptionBox();
            }
        }
        otherRequirements.getChildren().add(new OtherSubRequirement());
        otherRequirements.getChildren().add(new StackPane());
        otherRequirements.getChildren().add(addOtherSubRequirementButton);
    }

    @FXML
    private void addModuleRequirement() {
        modulePrereqs.getChildren().remove(addModPrereqButton);
        modulePrereqs.getChildren().add(new ModuleRequirement());
        modulePrereqs.getChildren().add(addModPrereqButton);
    }

    private ModulePrerequisiteList getModPrereqs(Parser parser) {
        ArrayList<ModulePrerequisite> modules = new ArrayList<>();

        // ignore the last child (button)
        for (int i = 0; i < modulePrereqs.getChildren().size() - 1; i++) {
            ModuleRequirement moduleRequirement = (ModuleRequirement) modulePrereqs.getChildren().get(i);
            String moduleCode = "";
            try {
                moduleCode = moduleRequirement.getCode();
            } catch (NullPointerException e) {
                continue;
            }
            String grade = moduleRequirement.getMinimumGrade();
            modules.add(parser.parseModulePrerequisite(moduleCode, grade));
        }
        return new ModulePrerequisiteList(modules);
    }
}


