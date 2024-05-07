import React, { useEffect, useState } from "react";
import { useTracked } from "react-tracked";
import AlertMessage from "./components/AlertMessage";
import { useSessionStorage } from "@uidotdev/usehooks";

export const BACKEND_URL =
  "http://" +
  process.env.REACT_APP_REST_API_HOST +
  ":" +
  process.env.REACT_APP_REST_API_PORT;

const AppConfig = () => {
  const [errorMessage, setErrorMessage] = useSessionStorage("errorMessage", "");
  const [globalProperties, setGlobalProperties] = useSessionStorage(
    "globalProperties",
    []
  );
  const [globalStatistics, setGlobalStatistics] = useSessionStorage(
    "globalStatistics",
    []
  );

  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  var requestOptions = {
    method: "POST",
    headers: myHeaders,
    redirect: "follow",
  };

  const fetchGlobalProperties = async () => {
    await fetch(BACKEND_URL + "/statistics?", requestOptions);
    const response = await fetch(BACKEND_URL + "/properties");
    let data = await response.json();
    let properties = data.map((prop) => prop.property);
    setGlobalProperties(properties);
  };

  const fetchGlobalStatistics = async () => {
    const response = await fetch(BACKEND_URL + "/statistics?", requestOptions);
    let data = await response.json();
    setGlobalStatistics(data);
  };

  const fetchHealth = async () => {
    try {
      const response = await fetch(BACKEND_URL + "/health");
      await response;
    } catch (error) {
      console.log(error);
      setErrorMessage("REST API is not accessible!");
    }
  };

  const fetchInitialData = async () => {
    await fetchHealth();
    await fetchGlobalProperties();
    await fetchGlobalStatistics();
  };

  useEffect(() => {
    fetchInitialData();
  }, []);

  return (
    <div>{errorMessage ? <AlertMessage message={errorMessage} /> : null}</div>
  );
};

export default AppConfig;
