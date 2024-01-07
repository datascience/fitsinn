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
  useEffect(() => {
    const fetchGet = async () => {
      try {
        const response = await fetch(BACKEND_URL + "/health");
        await response;
      } catch (error) {
        console.log(error);
        setErrorMessage("REST API is not accessible!");
      }
    };
    fetchGet();
  }, []);

  return (
    <div>{errorMessage ? <AlertMessage message={errorMessage} /> : null}</div>
  );
};

export default AppConfig;
