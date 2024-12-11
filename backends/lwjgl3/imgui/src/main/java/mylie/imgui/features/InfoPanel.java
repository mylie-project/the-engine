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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import mylie.engine.core.features.timer.Timer;
import mylie.imgui.ImGuiFeature;

public class InfoPanel extends ImGuiFeature {
    Timer timer;
    int windowLocation = 2;
    float[] frameTime;
    int currFrame = 0;
    List<DataGraph> dataGraphs = new ArrayList<>();
    List<AverageValueText> averageValues = new ArrayList<>();
    AverageValueText fps, cpu, memory, gpu, vram, heap;

    public InfoPanel() {
        super(InfoPanel.class);
    }

    @Override
    public void onInit() {
        super.onInit();
        timer = get(Timer.class);
        frameTime = new float[120 * 10];
        float graphResolution = 1 / 10f;
        int graphHistory = 50;
        dataGraphs.add(new DataGraph(
                "FrameTime",
                graphHistory,
                graphResolution,
                () -> (float) timer.time().delta() * 1000));
        dataGraphs.add(new DataGraph("Memory", graphHistory, graphResolution, () -> (float) getFreeRam()));
        dataGraphs.add(new DataGraph("Cpu Usage", graphHistory, graphResolution, () -> (float) getCpuUsage()));
        fps = new AverageValueText(
                "Fps", graphResolution, () -> (float) (1.0f / timer.time().delta()));
        cpu = new AverageValueText("Cpu", graphResolution, () -> (float) getCpuUsage());
        memory = new AverageValueText("Memory", graphResolution, () -> (float) getFreeRam());
        gpu = new AverageValueText("Gpu", graphResolution, () -> 0f);
        vram = new AverageValueText("Vram", graphResolution, () -> 0f);
        heap = new AverageValueText("Heap", graphResolution, () -> (float) getCurrentHeap());
        averageValues.add(fps);
        averageValues.add(cpu);
        averageValues.add(memory);
        averageValues.add(gpu);
        averageValues.add(vram);
        averageValues.add(heap);
    }

    @Override
    public void renderImGui() {
        Timer.Time time = timer.time();
        currFrame++;
        frameTime[currFrame % frameTime.length] = (float) (time.delta() * 1000);

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
        for (AverageValueText averageValue : averageValues) {
            averageValue.update(time);
        }
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
        fps.render(time);
        ImGui.tableNextColumn();
        cpu.render(time);
        ImGui.tableNextColumn();
        gpu.render(time);
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        heap.render(time);
        ImGui.tableNextColumn();
        memory.render(time);
        ImGui.tableNextColumn();
        vram.render(time);
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
    }

    private int getFreeRam() {
        // Get the free system memory in megabytes
        com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        long freePhysicalMemorySize = (osBean.getTotalMemorySize() - osBean.getFreeMemorySize()) / 1024 / 1024;
        return (int) freePhysicalMemorySize;
    }

    private int getCpuUsage() {
        java.lang.management.OperatingSystemMXBean osBean =
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean extendedOsBean) {
            return (int) (extendedOsBean.getCpuLoad() * 100);
        }
        return -1; // Default value if CPU usage cannot be retrieved
    }

    private int getCurrentHeap() {
        return (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);
    }

    static class AverageValueText {
        String title;
        float updateInterval;
        float data;
        float currData;
        int currDataCount;
        float currTime;
        Supplier<Float> dataSupplier;

        public AverageValueText(String title, float updateInterval, Supplier<Float> dataSupplier) {
            this.title = title;
            this.updateInterval = updateInterval;
            data = 0;
            this.dataSupplier = dataSupplier;
            currData = 0;
            currDataCount = 0;
            currTime = 0;
        }

        void update(Timer.Time time) {
            currTime += (float) time.delta();
            if (currTime > updateInterval) {
                currTime = 0;
                data = currData / currDataCount;
                currData = 0;
                currDataCount = 0;
            }
            currDataCount++;
            currData += dataSupplier.get();
        }

        void render(Timer.Time time) {
            ImGui.text(title + ": " + String.format("%d", (long) data));
        }
    }

    static class DataGraph {
        String title;
        float updateInterval;
        float[] data;
        float currData;
        int currDataCount;
        float currTime;
        Supplier<Float> dataSupplier;

        public DataGraph(String title, int historyLength, float updateInterval, Supplier<Float> dataSupplier) {
            this.title = title;
            this.updateInterval = updateInterval;
            data = new float[historyLength];
            this.dataSupplier = dataSupplier;
            currData = 0;
            currDataCount = 0;
            currTime = 0;
        }

        void update(Timer.Time time) {
            currTime += (float) time.delta();
            if (currTime > updateInterval) {
                currTime = 0;
                for (int i = 0; i < data.length - 1; i++) {
                    data[i] = data[i + 1];
                }
                data[data.length - 1] = currData / currDataCount;
                currData = 0;
                currDataCount = 0;
            }
            currDataCount++;
            currData += dataSupplier.get();
        }

        void render(Timer.Time time) {
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
