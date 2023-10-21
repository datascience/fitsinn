import { ColorModeContext, useMode } from "./theme";
import { CssBaseline, ThemeProvider } from "@mui/material";
import Topbar from "./scenes/global/Topbar";
import React, { useEffect, useState } from "react";
import Sidebar from "./scenes/global/Sidebar";

import Dashboard from "./scenes/dashboard";
import { Routes, Route } from "react-router-dom";
import Objects from "./scenes/objects";
import Invoices from "./scenes/invoices";
import ObjectDetails from "./scenes/objectDetails";
import Form from "./scenes/form";
import Line from "./scenes/line";
import Bar from "./scenes/bar";
import FAQ from "./scenes/faq";
import UploadForm from "./scenes/uploadForm";
import Geography from "./scenes/geography";
import Calendar from "./scenes/calendar";
import FilterPage from "./scenes/filterPage";
import { Provider } from "react-tracked";
import AppConfig from "./AppConfig";
import Treemap from "./scenes/treemap";
import Samples from "./scenes/samples";
const globalState = {
  filter: "",
  properties: {},
  operators: {},
  objectdetails: "",
};

const useValue = () =>
  React.useReducer(
    (state, newValue) => ({ ...state, ...newValue }),
    globalState
  );

const GlobalStateProvider = ({ children }) => (
  <Provider useValue={useValue}>{children}</Provider>
);

function App() {
  const [theme, colorMode] = useMode();

  return (
    <GlobalStateProvider>
      <ColorModeContext.Provider value={colorMode}>
        <ThemeProvider theme={theme}>
          <CssBaseline></CssBaseline>
          <div className="app">
            <Sidebar />

            <main className="content">
              <Topbar></Topbar>
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/objects" element={<Objects />} />
                <Route path="/invoices" element={<Invoices />} />
                <Route path="/objectdetails" element={<ObjectDetails />} />
                <Route path="/form" element={<Form />} />
                <Route path="/faq" element={<FAQ />} />
                <Route path="/bar" element={<Bar />} />
                <Route path="/samples" element={<Samples />} />
                <Route path="/treemap" element={<Treemap />} />
                <Route path="/filter" element={<FilterPage />} />
                <Route path="/line" element={<Line />} />
                <Route path="/geography" element={<Geography />} />
                <Route path="/calendar" element={<Calendar />} />
                <Route path="/upload" element={<UploadForm />} />
              </Routes>
              <AppConfig />
            </main>
          </div>
        </ThemeProvider>
      </ColorModeContext.Provider>
    </GlobalStateProvider>
  );
}

export default App;
