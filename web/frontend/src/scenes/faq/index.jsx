import { Box, useTheme, Typography } from "@mui/material";
import Header from "../../components/Header";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { tokens } from "../../theme";

const FAQ = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  return (
    <Box m="20px">
      <Header title="Help" subtitle="Frequently Asked Questions" />
      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography color={colors.greenAccent[500]} variant="h5">
            What is this tool about?
          </Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            This tool enables users to explore their digital objects and run
            content profiling.
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography color={colors.greenAccent[500]} variant="h5">
            What is content profiling?
          </Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            It is a set of activities within Digital Preservation to study
            digital collections based on characterisation results. Such results
            are obtained by proccesing the digital object using characterisation
            tools.
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography color={colors.greenAccent[500]} variant="h5">
            What is a characterisation tool?
          </Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>
            In Digital Preservation, a characterisation tool is used to extract
            metadata from the digital object and store it in a defined manner.
            Examples are DROID, Apache Tika, jHove. FITS combines several
            characterisation tools and produces a report in form of XML-file.
            This tool understands FITS outputs and can process such files
            directly.
          </Typography>
        </AccordionDetails>
      </Accordion>
      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography color={colors.greenAccent[500]} variant="h5">
            How to use this tool?
          </Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography>TBA.</Typography>
        </AccordionDetails>
      </Accordion>
    </Box>
  );
};

export default FAQ;
