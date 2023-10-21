import { useTheme } from "@mui/material";
import { Bar } from "@nivo/bar";
import { tokens } from "../theme";

const SimpleBarChart = ({ data, property, filterClick }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  return (
    <Bar
      data={data}
      theme={{
        // added
        axis: {
          domain: {
            line: {
              stroke: colors.grey[100],
            },
          },
          legend: {
            text: {
              fill: colors.grey[100],
            },
          },
          ticks: {
            line: {
              stroke: colors.grey[100],
              strokeWidth: 1,
            },
            text: {
              fill: colors.grey[100],
              fontSize: "12",
            },
          },
        },
        grid: {
          line: {
            strokeWidth: "0.5px",
          },
        },
        legends: {
          text: {
            fill: colors.grey[500],
          },
        },
        tooltip: {
          container: {
            color: "#141414",
          },
        },
      }}
      keys={["count"]}
      indexBy="value"
      valueScale={{ type: "symlog" }}
      reverse
      layout="horizontal"
      width={350}
      height={250}
      colors={colors.greenAccent[400]}
      enableGridY={false}
      margin={{ top: 10, bottom: 10, right: 250, left: 10 }}
      padding={0.3}
      borderColor={{
        from: "color",
        modifiers: [["darker", "1.6"]],
      }}
      axisBottom={null}
      axisLeft={null}
      axisRight={{
        tickSize: 3,
      }}
      labelSkipWidth={12}
      labelSkipHeight={12}
      labelTextColor={colors.grey[900]}
      onClick={(e) => filterClick(property, e)}
    />
  );
};

export default SimpleBarChart;
