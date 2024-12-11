package mylie.imgui.features;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxis;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.extension.implot.flag.ImPlotFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiWindowFlags;
import mylie.engine.core.features.timer.Timer;
import mylie.imgui.ImGuiFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InfoPanel extends ImGuiFeature {
    Timer timer;
    int windowLocation = 2;
    float[] frameTime;
    long[] heap;
    int currFrame = 0;
    List<DataGraph> dataGraphs = new ArrayList<>();
    public InfoPanel() {
        super(InfoPanel.class);
    }

    @Override
    public void onInit() {
        super.onInit();
        timer = get(Timer.class);
        frameTime = new float[120 * 10];
        heap = new long[120 * 10];
        dataGraphs.add(new DataGraph("FrameTime", 100, 1 / 10f, () -> (float) timer.time().delta()*1000));
        dataGraphs.add(new DataGraph("Memory", 100, 1 / 10f, () -> (float) getFreeRam()));
        dataGraphs.add(new DataGraph("Cpu Usage", 100, 1 / 10f, () -> (float) getCpuUsage()));
    }

    @Override
    public void renderImGui() {
        Timer.Time time = timer.time();
        currFrame++;
        frameTime[currFrame % frameTime.length] = (float) (time.delta() * 1000);
        heap[currFrame % frameTime.length] = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        ImGuiViewport viewport = ImGui.getMainViewport();
        ImVec2 workPos = viewport.getWorkPos();
        ImVec2 workSize = viewport.getWorkSize();
        ImVec2 windowPosition = new ImVec2();
        ImVec2 windowPivot = new ImVec2();
        float pad = 10;
        windowPosition.x = (windowLocation & 1) != 0 ? workPos.x + workSize.x - pad : workPos.x + pad;
        windowPosition.y = (windowLocation & 2) != 0 ? workPos.y + workSize.y - pad : workPos.y + pad;
        windowPivot.x = (windowLocation & 1) != 0 ? 1 : 0;
        windowPivot.y = (windowLocation & 2) != 0 ? 1 : 0;
        ImGui.setNextWindowPos(windowPosition, ImGuiCond.Always, windowPivot);
        ImGui.setNextWindowSize(new ImVec2(400, 0));
        ImGui.begin(
                "MyLiE Engine",
                ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.text("MyLiE - Control Panel");
        ImGui.separator();
        ImGui.beginTable("InfoTable", 4);
        ImGui.tableSetupColumn("Column 0", ImGuiTableColumnFlags.WidthFixed, 95.0f);
        ImGui.tableSetupColumn("Column 1", ImGuiTableColumnFlags.WidthFixed, 95.0f);
        ImGui.tableSetupColumn("Column 2", ImGuiTableColumnFlags.WidthFixed, 95.0f);
        ImGui.tableSetupColumn("Column 3", ImGuiTableColumnFlags.WidthFixed, 95.0f);
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        ImGui.text("Fps: " + (int) (1d / time.delta()));
        ImGui.tableNextColumn();
        ImGui.text("Cpu: " + getCpuUsage());
        ImGui.tableNextColumn();
        ImGui.text("Gpu: " + "n/a");

        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        ImGui.text("Heap: " + heap[currFrame % frameTime.length] + "MB");
        ImGui.tableNextColumn();
        ImGui.text("Ram: " + getFreeRam() + " mb");
        ImGui.tableNextColumn();
        ImGui.text("VRam: n/a");
        ImGui.tableNextColumn();
        ImGui.button("Settings");
        ImGui.tableNextRow();
        ImGui.endTable();
        ImGui.separator();
        for (DataGraph dataGraph : dataGraphs) {
            dataGraph.update(time);
            if (ImGui.collapsingHeader(dataGraph.title)) {
                dataGraph.render(time);
            }
        }
        ImGui.end();
        /*if (ImGui.collapsingHeader("FrameTime Graph")) {
            ImPlot.beginPlot(
                    "Frametime",
                    new ImVec2(390, 100),
                    ImPlotFlags.NoMenus | ImPlotFlags.NoInputs | ImPlotFlags.NoTitle | ImPlotFlags.NoLegend);
            ImPlot.setupAxis(
                    ImPlotAxis.Y1, "ms", ImPlotAxisFlags.AutoFit | ImPlotAxisFlags.NoMenus | ImPlotAxisFlags.NoLabel);
            ImPlot.setupAxis(
                    ImPlotAxis.X1,
                    "ms",
                    ImPlotAxisFlags.AutoFit
                            | ImPlotAxisFlags.NoMenus
                            | ImPlotAxisFlags.NoLabel
                            | ImPlotAxisFlags.NoDecorations);
            ImPlot.plotLine("Frametime", frameTime, frameTime.length);
            ImPlot.plotVLines("Frame", new int[]{currFrame % frameTime.length});
            ImPlot.endPlot();
        }
        if (ImGui.collapsingHeader("Heap Graph")) {
            ImPlot.beginPlot(
                    "Heap",
                    new ImVec2(390, 100),
                    ImPlotFlags.NoMenus | ImPlotFlags.NoInputs | ImPlotFlags.NoTitle | ImPlotFlags.NoLegend);
            ImPlot.setupAxis(
                    ImPlotAxis.Y1, "ms", ImPlotAxisFlags.AutoFit | ImPlotAxisFlags.NoMenus | ImPlotAxisFlags.NoLabel);
            ImPlot.setupAxis(
                    ImPlotAxis.X1,
                    "ms",
                    ImPlotAxisFlags.AutoFit
                            | ImPlotAxisFlags.NoMenus
                            | ImPlotAxisFlags.NoLabel
                            | ImPlotAxisFlags.NoDecorations);
            ImPlot.plotLine("Heap", heap, heap.length);
            ImPlot.plotVLines("Frame", new int[]{currFrame % frameTime.length});
            ImPlot.endPlot();
        }

        if (ImGui.beginPopupContextWindow()) {
            if (ImGui.menuItem("Top Left")) windowLocation = 0;
            if (ImGui.menuItem("Top Right")) windowLocation = 1;
            if (ImGui.menuItem("Bottom Left")) windowLocation = 2;
            if (ImGui.menuItem("Bottom Right")) windowLocation = 3;
            ImGui.endPopup();
        }
        ;*/
    }

    private int getFreeRam() {
// Get the free system memory in megabytes
        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        long freePhysicalMemorySize = osBean.getFreeMemorySize() / 1024 / 1024;
        return (int) freePhysicalMemorySize;
    }

    private int getCpuUsage() {
        java.lang.management.OperatingSystemMXBean osBean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean extendedOsBean) {
            return (int) (extendedOsBean.getCpuLoad() * 100);
        }
        return -1; // Default value if CPU usage cannot be retrieved
    }

    static class DataGraph{
        String title;
        float updateInterval;
        float[] data;
        float currData;
        int currDataCount;
        float currTime;
        Supplier<Float> dataSupplier;
        public DataGraph(String title,int historyLength, float updateInterval, Supplier<Float> dataSupplier) {
            this.title = title;
            this.updateInterval = updateInterval;
            data = new float[historyLength];
            this.dataSupplier = dataSupplier;
            currData = 0;
            currDataCount = 0;
            currTime = 0;
        }

        void update(Timer.Time time){
            currTime+= (float) time.delta();
            if(currTime > updateInterval){
                currTime=0;
                for (int i = 0; i < data.length-1; i++) {
                    data[i] = data[i+1];
                }
                data[data.length - 1] = currData/currDataCount;
                currData=0;
                currDataCount=0;
            }
            currDataCount++;
            currData+=dataSupplier.get();

        }

        void render(Timer.Time time){
            ImPlot.beginPlot(
                    title,
                    new ImVec2(390, 100),
                    ImPlotFlags.NoMenus | ImPlotFlags.NoInputs | ImPlotFlags.NoTitle | ImPlotFlags.NoLegend);
            ImPlot.setupAxis(
                    ImPlotAxis.Y1, "ms", ImPlotAxisFlags.AutoFit | ImPlotAxisFlags.NoMenus | ImPlotAxisFlags.NoLabel);
            ImPlot.setupAxis(
                    ImPlotAxis.X1,
                    "ms",
                    ImPlotAxisFlags.AutoFit
                            | ImPlotAxisFlags.NoMenus
                            | ImPlotAxisFlags.NoLabel
                            | ImPlotAxisFlags.NoDecorations);
            ImPlot.plotLine(title, data, data.length);
            ImPlot.endPlot();
        }
    }
}
