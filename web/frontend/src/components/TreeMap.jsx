import { ResponsiveTreeMap } from "@nivo/treemap";
import { useTheme } from "@mui/material";
import { tokens } from "../theme";
import { mockTreemap } from "../data/mockTreemap";

const TreeMap = ({
  treemapdata,
  isCustomLineColors = false,
  isDashboard = false,
}) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  //var data = mockTreemap;
  return (
    <ResponsiveTreeMap
      data={treemapdata}
      identity="name"
      value="value"
      margin={{ top: 10, right: 10, bottom: 10, left: 10 }}
      label={(e) => e.formattedValue + " " + e.id}
      labelSkipSize={12}
      labelTextColor={{
        from: "color",
        modifiers: [["darker", 5]],
      }}
      parentLabelPosition="left"
      parentLabelTextColor={{
        from: "color",
        modifiers: [["darker", 2]],
      }}
      borderColor={{
        from: "color",
        modifiers: [["darker", 0.1]],
      }}
    />
  );
};

export default TreeMap;
